import { PrettyLogger } from "../src/pretty-logger";

test("testPrettyLogger", () => {
    const log = jest.spyOn(console, "log");
    const outputs: string[] = [];
    log.mockImplementation((x) => outputs.push(x as string));

    const logger = new PrettyLogger();
    logger.add("a", "hello world");
    logger.add("text", "see you");
    logger.add("bot", "logged your message");
    logger.add("human", "test test");
    logger.add("app", "ya");
    logger.execute();

    expect(outputs).toStrictEqual([
        "a      hello world",
        "text   see you",
        "bot    logged your message",
        "human  test test",
        "app    ya",
    ]);

    log.mockReset();
    log.mockRestore();
});
