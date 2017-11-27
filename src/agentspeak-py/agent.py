#!/usr/bin/python
# -*- coding: utf-8 -*-

from environment import *

#check what to expect
walk = parse_literal('')


class Pucrs(Environment):
    def __init__(self):
        Environment.__init__(self)
        self.add_percept(ld)

    def execute_action(self, agent_name, action):
        print("[%s] Doing %s" % (agent_name, action));
        self.clear_perceptions()

        getattr(self, action.functor)(action.args)

    def walk(self, *args):
        self.add_percept(walk)

        