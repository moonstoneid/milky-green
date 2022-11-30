DOCKER_BUILDKIT=1 docker-compose \
  --project-name milkygreen \
  --file docker-compose.yml \
  up --build --detach