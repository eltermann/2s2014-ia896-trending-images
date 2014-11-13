#!/usr/bin/python

import os
import sys
sys.path.append(os.path.join(os.path.dirname(__file__), '..'))

from timgs.utils.collect import update_summary

update_summary()
