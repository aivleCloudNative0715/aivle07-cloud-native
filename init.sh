#!/bin/bash
set -e

echo "🔄 Updating APT package lists..."
sudo apt-get update

echo "📦 Installing basic networking tools..."
sudo apt-get install -y net-tools iputils-ping curl gnupg2 python3-venv pipx

echo "🌐 Installing HTTPie via pipx (가상환경 기반)..."
pipx ensurepath
pipx install httpie

echo "⬇️ Installing kubectl..."
curl -LO "https://dl.k8s.io/release/$(curl -Ls https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
rm kubectl

echo "🔐 Adding Microsoft GPG key..."
curl -sL https://packages.microsoft.com/keys/microsoft.asc |
  gpg --dearmor |
  sudo tee /etc/apt/trusted.gpg.d/microsoft.gpg > /dev/null

echo "📝 Adding Azure CLI repo..."
echo "deb [arch=amd64] https://packages.microsoft.com/repos/azure-cli/ jammy main" |
  sudo tee /etc/apt/sources.list.d/azure-cli.list > /dev/null

echo "🔄 Updating APT after adding Azure CLI repo..."
sudo apt-get update

echo "☁️ Installing Azure CLI..."
sudo apt-get install -y azure-cli

echo "🐳 Installing Docker Compose (plugin)..."
sudo apt-get install -y docker-compose-plugin

echo "🟢 Installing NVM (Node Version Manager)..."
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.38.0/install.sh | bash

# NVM 환경 변수 설정
export NVM_DIR="$HOME/.nvm"
. "$NVM_DIR/nvm.sh"

echo "🟢 Installing Node.js v14.19.0..."
nvm install 14.19.0
nvm use 14.19.0
nvm alias default 14.19.0
export NODE_OPTIONS=--openssl-legacy-provider

echo "🚀 Running Docker Compose in /infra..."
cd infra
docker compose up -d

echo "✅ All tools installed and containers started successfully!"
