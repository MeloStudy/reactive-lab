.PHONY: setup test clean clean-all help

# Central router Makefile for Reactive Lab
# Use it like: make test LAB=001

# Shell configuration for Windows compatibility
PWSH = powershell -ExecutionPolicy Bypass -Command

help:
	@echo "Reactive Programming Laboratory - CLI Tool"
	@echo "------------------------------------------"
	@echo "Usage:"
	@echo "  make setup LAB=XXX   - Spin up infrastructure (if any) for a lab"
	@echo "  make test LAB=XXX    - Run validation suite (Node or Maven)"
	@echo "  make clean LAB=XXX   - Tear down lab environment"
	@echo "  make clean-all       - EMERGENCY: Stop all reactive_lab containers"
	@echo ""
	@echo "Example: make setup LAB=001"

check-lab:
ifndef LAB
	$(error Error: You must specify the laboratory (e.g., make setup LAB=001))
endif

setup: check-lab
	@echo "=> Spinning up infrastructure for LAB-$(LAB)..."
	@$(PWSH) "$$labDir = Get-ChildItem -Path labs -Filter '$(LAB)-*' | Select-Object -ExpandProperty FullName; if (Test-Path \"$$labDir\docker-compose.yml\") { cd $$labDir; docker-compose up -d } else { echo 'No docker-compose found, skipping setup.' }"

test: check-lab
	@echo "=> Running tests for LAB-$(LAB)..."
	@$(PWSH) "$$labDir = Get-ChildItem -Path labs -Filter '$(LAB)-*' | Select-Object -ExpandProperty FullName; if (Test-Path \"$$labDir\pom.xml\") { cd $$labDir; mvn test } else { echo 'No Maven project (pom.xml) found for this lab.' }"

clean: check-lab
	@echo "=> Tearing down LAB-$(LAB)..."
	@$(PWSH) "$$labDir = Get-ChildItem -Path labs -Filter '$(LAB)-*' | Select-Object -ExpandProperty FullName; if (Test-Path \"$$labDir\docker-compose.yml\") { cd $$labDir; docker-compose down -v --remove-orphans } else { echo 'No docker-compose found, skipping cleanup.' }"

clean-all:
	@echo "=> EMERGENCY: Cleaning all lab containers..."
	@$(PWSH) "docker ps -a --filter 'name=reactive_lab' -q | ForEach-Object { docker stop $$_; docker rm $$_ }"
	@echo "=> All lab containers stopped and removed."
