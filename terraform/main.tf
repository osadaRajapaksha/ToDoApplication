provider "aws" {
  region = var.aws_region
}

resource "aws_security_group" "app_sg" {
  name        = "todoapp_sg"
  description = "Allow inbound traffic for ToDo app"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 3000
    to_port     = 3000
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
              # Update packages and install docker & git
              dnf update -y
              dnf install -y git docker
              systemctl start docker
              systemctl enable docker
              
              # Install docker-compose
              curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
              chmod +x /usr/local/bin/docker-compose
              
              # Clone the repository
              cd /home/ec2-user
              git clone https://github.com/osadaRajapaksha/ToDoApplication.git
              cd ToDoApplication
              
              # Fetch instance's public IP using IMDSv2
              TOKEN=$(curl -X PUT "http://169.254.169.254/latest/api/token" -H "X-aws-ec2-metadata-token-ttl-seconds: 21600")
              PUBLIC_IP=$(curl -H "X-aws-ec2-metadata-token: $TOKEN" -s http://169.254.169.254/latest/meta-data/public-ipv4)
              
              # Write environment variables
              cat <<EOT > .env
              SPRING_DATA_MONGODB_URI=${var.mongodb_uri}
              JWT_SECRET=thisisasecretkeywhichshouldbeatleast256bitslongsothatitworks
              NEXT_PUBLIC_API_URL=http://$PUBLIC_IP:8080/api
              EOT
              
              # Build and run containers
              /usr/local/bin/docker-compose up -d --build
              EOF

  tags = {
    Name = "ToDoApp-Instance"
  }
}
