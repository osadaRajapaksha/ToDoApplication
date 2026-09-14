output "backend_url" {
  value = "http://${aws_instance.app_instance.public_ip}:8080"
}
output "frontend_s3_bucket" {
  value = aws_s3_bucket.frontend_bucket.bucket
}
output "frontend_cloudfront_url" {
  value = "https://${aws_cloudfront_distribution.frontend_distribution.domain_name}"
}
