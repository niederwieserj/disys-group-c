# Energy Community

Distributed Systems semester project that simulates an energy community.

The system consists of six independently startable components communicating through RabbitMQ, PostgreSQL and a REST API.

## Architecture

```text
Energy Producer ─┐
                 ├── RabbitMQ ──> Usage Service ──> PostgreSQL
Energy User ─────┘                     │
                                      └── RabbitMQ
                                           │
                                           v
                              Current Percentage Service
                                           │
                                           v
                                      PostgreSQL
                                           │
                                           v
JavaFX GUI ── HTTP GET ──> REST API ───────┘