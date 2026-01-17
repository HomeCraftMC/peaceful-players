#!/bin/bash
set -e

JAR_FILE=$(ls target/PeacefulPlayers-*.jar 2>/dev/null | head -1)
S3_BUCKET="minecraft-plugins"
S3_ENDPOINT="https://s3.psalkowski.pl"
S3_KEY="PeacefulPlayers.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "Error: $JAR_FILE not found. Run 'mvn clean package' first."
    exit 1
fi

echo "Uploading $JAR_FILE to $S3_ENDPOINT/$S3_BUCKET/$S3_KEY..."

curl -X PUT -T "$JAR_FILE" \
  --aws-sigv4 "aws:amz:home:s3" \
  -u "${S3_USER}:${S3_PASS}" \
  "$S3_ENDPOINT/$S3_BUCKET/$S3_KEY"

echo ""
echo "Upload complete!"
echo "Public URL: $S3_ENDPOINT/$S3_BUCKET/$S3_KEY"

echo ""
echo "Verifying upload..."
curl -sI "$S3_ENDPOINT/$S3_BUCKET/$S3_KEY" | head -5
