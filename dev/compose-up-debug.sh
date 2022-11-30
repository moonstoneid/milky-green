DOCKER_BUILDKIT=1 docker-compose \
  --project-name milkygreen \
  --file docker-compose.yml --file docker-compose-debug.yml \
  up --build --detach