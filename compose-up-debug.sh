./mvnw package

docker-compose \
  --project-name web3-login \
  --file docker-compose.yml --file docker-compose-debug.yml \
  up --build --detach