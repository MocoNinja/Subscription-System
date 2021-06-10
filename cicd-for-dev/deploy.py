#!/usr/bin/python3

from sys import argv as args, exit, path as sys_path
from os import system as bash, chdir
from time import sleep

## Names for every project we want to build and deploy
BACKEND = "backend-service"
SUBSCRIPTION = "subscription-service"
EMAIL="email-service"
DATABASE="database"


## Al images will be tagged as "DEV" by default
DEFAULT_TAG="DEV"

## dockerhub to push the image to
DEFAULT_REPO="moconinja"

## If an error happens in the pipeline, do we wish to continue?
EXIT_ON_FAIL=True

## Script's path, contains yml
ROOTDIR=sys_path[0]

## Names for docker-compose files
APPS=f"{ROOTDIR}/docker-compose-apps.yml"
REQUIREMENTS=f"{ROOTDIR}/docker-compose-requirements.yml"

## Delays
DELAY=10

def parse_args():
    to_build = []
    TAG = DEFAULT_TAG
    REPO = DEFAULT_REPO

    if "--help" in args:
        help()
    elif "--end" in args:
        end()
        exit(0)
    elif "--terminate" in args:
        end()
        terminate()
        exit(0)
    else:
        if "--all" in args:
            to_build = [BACKEND, SUBSCRIPTION, EMAIL, DATABASE]
        else:
            if "--backend" in args:
                to_build.append(BACKEND)
            if "--subscription" in args:
                to_build.append(SUBSCRIPTION)
            if "--email" in args:
                to_build.append(EMAIL)
            if "--database" in args:
                to_build.append(DATABASE)
        if "--version" in args:
            try:
                index = args.index("--version")
                TAG = args[index + 1]
            except:
                print("Wrong tag, using default")
        if "--repo" in args:
            try:
                index = args.index("--repo")
                REPO = args[index + 1]
            except:
                print("Wrong repo, using default")
        if not "--push" in args:
            print("No --push flag, so skipping the pushing...")
            REPO = None

    if len(to_build) == 0:
        to_build = [BACKEND, SUBSCRIPTION, EMAIL, DATABASE]

    return {"BUILDS": to_build, "TAG": TAG, "REPO": REPO}

def help():
    msg = """
    Usage .{script} [OPTIONS]
    OPTIONS:
        --help --> displays this help
        --version (version) --> tags the images with the specified version [DEFAULTS TO {tag}]
        --backend           --> builds the backend microservice
        --subscription      --> builds the subscriptions microservice
        --email             --> builds the email microservice
        --database          --> builds the database
        --all (default)     --> builds everything
        --push              --> if enabled, pushes the images to a repository
        --repo              --> the docker repository where the images will be pushed if the previous flag is set
        --end               --> stops docker compose stacks
        --terminate         --> --ends and executes docker system prune (which might be what you want!!!)
    """.format(script = args[0], tag = DEFAULT_TAG)
    print(msg)
    exit(0)

def build_docker_image(microservice, tag, REPO):
    print(f"Building image {microservice}:{tag}")
    exec_command = f"docker build {microservice} -t {microservice}:{tag} -f {microservice}/Dockerfile"
    print(f"Launching {exec_command}...")
    code = bash(exec_command)
    if code == 0:
        print("Execution OK")
        if REPO is not None:
            print("Pushing to registry...")
            exec_command = f"docker tag {microservice}:{tag} {REPO}/{microservice}:{tag} && docker push {REPO}/{microservice}:{tag}"
            bash(exec_command)
    else:
        print("Execution KO")
        if EXIT_ON_FAIL:
            print("Pipeline is configured to exit on failure, so bye bye...")
            exit(1)

def end():
    print("Stopping requirements...")
    bash(f"docker-compose -f {REQUIREMENTS} down")

    print(f"Waiting {DELAY} seconds...")
    sleep(DELAY)

    print("Stopping Apps...")
    bash(f"docker-compose -f {APPS} down")

def terminate():
    print("T E R M I N A T I N G")
    bash(f"echo 'y' | docker system prune")

if __name__ == "__main__":
    config = parse_args()
    print("Building images...")

    chdir(ROOTDIR)
    chdir("..")

    for microservice in config.get("BUILDS"):
        build_docker_image(microservice, config.get("TAG"), config.get("REPO"))

    bash("docker network create subscriptions_network")
    
    print("Deploying requirements...")
    bash(f"docker-compose -f {REQUIREMENTS} up -d")

    print(f"Waiting {DELAY} seconds to avoid eager connection issues...")
    sleep(DELAY)

    print("Deploying Apps...")
    bash(f"docker-compose -f {APPS} up -d")

