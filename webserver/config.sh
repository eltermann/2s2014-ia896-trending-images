#!/bin/bash

mkdir -p /var/log/
mkdir -p /var/www/ia896
ln -s `pwd`/webserver.py /var/www/ia896/webserver.py
ln -s `pwd`/supervisor.conf /etc/supervisor/conf.d/ia896.conf
