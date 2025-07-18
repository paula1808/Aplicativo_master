terraform {
  required_version = ">= 1.3.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "us-east-1"
}

resource "aws_eks_cluster" "cluster" {
  name     = "sistema-academico-eks"
  role_arn = "arn:aws:iam::711616153649:role/LabRole"

  vpc_config {
    subnet_ids = ["subnet-0b485d34e92bbdf20",
"subnet-044fb73d4d107165b",
"subnet-0e8335be67530a00d",
"subnet-08dbb50c6e579f18b",
"subnet-0ae99c6a2db29e1e2"]
  }

  version = "1.28"
}

resource "aws_eks_node_group" "node_group" {
  cluster_name    = aws_eks_cluster.cluster.name
  node_group_name = "default"
  node_role_arn   = "arn:aws:iam::711616153649:role/LabRole"
  subnet_ids = ["subnet-0b485d34e92bbdf20",
"subnet-044fb73d4d107165b",
"subnet-0e8335be67530a00d",
"subnet-08dbb50c6e579f18b",
"subnet-0ae99c6a2db29e1e2"]

  scaling_config {
    desired_size = 2
    max_size     = 3
    min_size     = 1
  }

  instance_types = ["t2.large"]
}

