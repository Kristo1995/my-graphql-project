SERVICE_NAME=my-graphql-project
IMAGE_NAME=${SERVICE_NAME}-img

build:
	docker build -t ${IMAGE_NAME} .