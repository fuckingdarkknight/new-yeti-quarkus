#!/bin/bash

echo '{ "action": "shutdown" }' > /dev/tcp/127.0.0.1/61617
