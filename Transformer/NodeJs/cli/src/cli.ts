#! /usr/bin/env node

import { TransformCommand } from "./transform-command";
import { Runner } from "bibliothekar-core-cli";

const runner = new Runner([new TransformCommand()]);
runner.run();
