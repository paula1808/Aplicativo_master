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
    subnet_ids = ["subnet-0d785769ce64bdce8","subnet-07e1e6b4dd2893b27","subnet-0ce441e8c985b81a1","subnet-033f37c1eb864e5bd","subnet-0d2011ba5d5db817f"]
  }

  version = "1.28"
}

resource "aws_eks_node_group" "node_group" {
  cluster_name    = aws_eks_cluster.cluster.name
  node_group_name = "default"
  node_role_arn   = "arn:aws:iam::711616153649:role/LabRole"
  subnet_ids = [
    "subnet-0d785769ce64bdce8",
    "subnet-07e1e6b4dd2893b27",
    "subnet-0ce441e8c985b81a1",
    "subnet-033f37c1eb864e5bd",
    "subnet-0d2011ba5d5db817f"
  ]

  scaling_config {
    desired_size = 2
    max_size     = 3
    min_size     = 1
  }

  instance_types = ["t3.medium"]
}

