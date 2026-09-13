variable "aws_region" {
  default = "us-east-1"
}
variable "mongodb_uri" {
  description = "MongoDB Connection String"
  type        = string
  sensitive   = true
}
