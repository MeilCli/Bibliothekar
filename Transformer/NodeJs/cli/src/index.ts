import * as process from "process";
import * as path from "path";
import { Pom, PomReader, PomTransformer } from "bibliothekar-transformer-core";

const reportPath = path.resolve(
    process.cwd(),
    "../../../Extractor/Gradle/sandbox/jvm-child/build/output/bibliothekar/report/bibliothekarReport"
);
const reader = new PomReader();
const poms = reader.read(reportPath);
const transformer = new PomTransformer();
for (const pom of poms) {
    console.log(transformer.transform(pom));
}
