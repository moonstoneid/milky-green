DOCKER_BUILDKIT=1 docker-compose \
  --project-name web3-login \
  --file docker-compose.yml \
  up --build --detach