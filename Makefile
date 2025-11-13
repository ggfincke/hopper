COMPOSE ?= docker compose
COMPOSE_FILE ?= docker-compose.yml
ENV_DIR ?= env
ENV_FILES = api.env db.env frontend.env marketplace.env
SERVICE ?=

.PHONY: ensure-env dev-up dev-down dev-logs dev-ps dev-restart dev-clean api-shell marketplace-shell api-image marketplace-image compose-config

ensure-env:
	@missing=0; \
	for file in $(ENV_FILES); do \
		if [ ! -f $(ENV_DIR)/$$file ]; then \
			echo "[env] missing $(ENV_DIR)/$$file (copy from $(ENV_DIR)/.sample/$$file)"; \
			missing=1; \
		fi; \
	done; \
	if [ $$missing -ne 0 ]; then \
		exit 1; \
	fi

dev-up: ensure-env
	$(COMPOSE) -f $(COMPOSE_FILE) up --build --detach

dev-down:
	$(COMPOSE) -f $(COMPOSE_FILE) down --remove-orphans

dev-restart:
	$(COMPOSE) -f $(COMPOSE_FILE) up --build --detach --force-recreate

dev-logs:
	$(COMPOSE) -f $(COMPOSE_FILE) logs -f $(SERVICE)

dev-ps:
	$(COMPOSE) -f $(COMPOSE_FILE) ps

dev-clean:
	$(COMPOSE) -f $(COMPOSE_FILE) down --volumes --remove-orphans

api-shell:
	$(COMPOSE) -f $(COMPOSE_FILE) exec api /bin/sh

marketplace-shell:
	$(COMPOSE) -f $(COMPOSE_FILE) exec marketplace /bin/sh

api-image:
	$(COMPOSE) -f $(COMPOSE_FILE) build api

marketplace-image:
	$(COMPOSE) -f $(COMPOSE_FILE) build marketplace

compose-config:
	$(COMPOSE) -f $(COMPOSE_FILE) config
