variable "aws_region" {
  default = "us-east-1"
}

variable "cluster_name" {
  default = "sistema-academico-cluster"
}

variable "vpc_id" {
  description = "VPC ID donde desplegar el clúster"
  type        = string
}

variable "subnet_ids" {
  description = "Lista de subredes públicas"
  type        = list(string)
}
