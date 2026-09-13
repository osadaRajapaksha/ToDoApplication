provider "aws" {
  region = var.aws_region
}

# ----------------- BACKEND EC2 -----------------

resource "aws_security_group" "app_sg" {
  name_prefix = "todoapp_backend_sg-"
  description = "Allow inbound traffic for ToDo app backend"
  
  lifecycle {
    create_before_destroy = true
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]
  filter {
    name   = "name"
    values = ["al2023-ami-2023.*-x86_64"]
  }
}

resource "aws_instance" "app_instance" {
  ami           = data.aws_ami.amazon_linux.id
  instance_type = "t3.small"
  vpc_security_group_ids = [aws_security_group.app_sg.id]

  user_data = <<-EOF
              #!/bin/bash
              dnf update -y
              dnf install -y git docker
              systemctl start docker
              systemctl enable docker
              
              curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
              chmod +x /usr/local/bin/docker-compose
              
              cd /home/ec2-user
              git clone https://github.com/osadaRajapaksha/ToDoApplication.git
              cd ToDoApplication
              
              cat <<EOT > .env
              SPRING_DATA_MONGODB_URI=${var.mongodb_uri}
              JWT_SECRET=thisisasecretkeywhichshouldbeatleast256bitslongsothatitworks
              EOT
              
              /usr/local/bin/docker-compose up -d --build
              EOF

  tags = {
    Name = "ToDoApp-Backend"
  }
}

# ----------------- FRONTEND S3 + CLOUDFRONT -----------------

resource "random_id" "bucket_suffix" {
  byte_length = 4
}

resource "aws_s3_bucket" "frontend_bucket" {
  bucket        = "todoapp-frontend-${random_id.bucket_suffix.hex}"
  force_destroy = true
}

resource "aws_s3_bucket_website_configuration" "frontend_website" {
  bucket = aws_s3_bucket.frontend_bucket.id
  index_document {
    suffix = "index.html"
  }
  error_document {
    key = "index.html" # Next.js SPA routing
  }
}

resource "aws_s3_bucket_public_access_block" "frontend_bucket_access" {
  bucket = aws_s3_bucket.frontend_bucket.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_policy" "frontend_bucket_policy" {
  bucket = aws_s3_bucket.frontend_bucket.id
  depends_on = [aws_s3_bucket_public_access_block.frontend_bucket_access]

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "PublicReadGetObject"
        Effect    = "Allow"
        Principal = "*"
        Action    = "s3:GetObject"
        Resource  = "${aws_s3_bucket.frontend_bucket.arn}/*"
      }
    ]
  })
}

resource "aws_cloudfront_distribution" "frontend_distribution" {
  origin {
    domain_name = aws_s3_bucket.frontend_bucket.bucket_regional_domain_name
    origin_id   = "todoapp-frontend-s3"
  }

  enabled             = true
  is_ipv6_enabled     = true
  default_root_object = "index.html"

  default_cache_behavior {
    allowed_methods  = ["GET", "HEAD", "OPTIONS"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = "todoapp-frontend-s3"

    forwarded_values {
      query_string = false
      cookies {
        forward = "none"
      }
    }

    viewer_protocol_policy = "allow-all"
    min_ttl                = 0
    default_ttl            = 3600
    max_ttl                = 86400
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true
  }
  
  custom_error_response {
    error_code            = 403
    response_page_path    = "/index.html"
    response_code         = 200
    error_caching_min_ttl = 10
  }
  
  custom_error_response {
    error_code            = 404
    response_page_path    = "/index.html"
    response_code         = 200
    error_caching_min_ttl = 10
  }
}

# ----------------- LOCAL BUILD EXECUTION -----------------

resource "null_resource" "build_and_deploy_frontend" {
  # Trigger when the backend IP changes
  triggers = {
    backend_ip = aws_instance.app_instance.public_ip
  }

  provisioner "local-exec" {
    # Use powershell on windows
    interpreter = ["PowerShell", "-Command"]
    command = <<-EOT
      cd ../frontend
      npm install
      $env:NEXT_PUBLIC_API_URL="http://${aws_instance.app_instance.public_ip}:8080/api"
      npm run build
      aws s3 sync out/ s3://${aws_s3_bucket.frontend_bucket.bucket} --delete
    EOT
  }
  
  depends_on = [
    aws_cloudfront_distribution.frontend_distribution
  ]
}
