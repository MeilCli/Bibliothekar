import * as process from "process";
import * as path from "path";
import { Command, HelpCommand, Argument, Context, StringArgument, BooleanArgument } from "bibliothekar-core-cli";
import { NoticeWriter } from "bibliothekar-core-core";
import { PomReader, PomTransformer } from "bibliothekar-transformer-core";

export class TransformCommand extends Command {
    name = "transform";
    description = "transform to notice files from any files that extract from package manager";
    fromArgument = new StringArgument("--from", ["-f"], "input path", true, "");
    toArgument = new StringArgument("--to", ["-t"], "output path", true, "");
    dryRunArgument = new BooleanArgument("--dry-run", ["-d"], "dry-run", false, false);
    argumentList: Argument<unknown>[] = [this.fromArgument, this.toArgument, this.dryRunArgument];
    subCommands: Command[] = [new HelpCommand(this)];

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    execute(context: Context) {
        const inputPath = path.resolve(process.cwd(), this.fromArgument.value);
        const outputPath = path.resolve(process.cwd(), this.toArgument.value);
        const reader = new PomReader();
        const writer = new NoticeWriter(outputPath);
        const poms = reader.read(inputPath);
        const transformer = new PomTransformer();

        const notices = poms.map((x) => transformer.transform(x));

        if (this.dryRunArgument.value) {
            for (const notice of writer.willOverrides(notices)) {
                console.log(`will override: ${notice.packageId}`);
            }
            for (const path of writer.willWrites(notices)) {
                console.log(`will write: ${path}`);
            }
        } else {
            writer.write(notices);
            console.log(`write ${notices.length} packages to ${outputPath}`);
        }
    }
}
