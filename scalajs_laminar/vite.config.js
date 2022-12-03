import { spawnSync } from "child_process";
import { defineConfig } from "vite";

function isDev() { return process.env.NODE_ENV !== "production"; }
function printMillOutputDir(task) {
  const millModuleDir = process.cwd();
  process.chdir("../");
  const args = ["--silent", "show", `scalajs_laminar.${task}`];
  const options = {
    cwd: process.cwd(),
    stdio: [
      "pipe", // StdIn.
      "pipe", // StdOut.
      "inherit", // StdErr.
    ],
  };
  const result = spawnSync("mill", args, options);
  process.chdir(millModuleDir);

  if (result.error)
    throw result.error;
  else if (result.status !== 0)
    throw new Error(`mill process failed with exit code ${result.status}`);
  else {
    const out = JSON.parse(result.stdout);
    const dest = out.dest.toString("utf8").split(":")[2].trim();
    return dest.trim();
  }

}
const linkOutputDir = isDev()
  ? printMillOutputDir("fastLinkJS")
  : printMillOutputDir("fullLinkJS");

export default defineConfig({
  resolve: {
    alias: [ { find: "@linkOutputDir", replacement: linkOutputDir } ]
  }
});
