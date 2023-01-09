import { ShowCommand } from "./show-command";
import { Runner } from "bibliothekar-core-cli";

const runner = new Runner([new ShowCommand()]);
runner.run();
