// runAll.js
import { spawn } from "node:child_process";
import { existsSync } from "node:fs";
import path from "node:path";

// ─────────────────────────────────────────────────────────────
// CONFIG
// ─────────────────────────────────────────────────────────────
const BASE_URL = "http://localhost:8081";

const SEED_SCRIPTS = [
  { file: "testAddAsset.js",                label: "Assets" },
  { file: "testCreateStock.js",             label: "Stock Watchlist" },
  { file: "testAddMutualFundWatchList.js",  label: "Mutual Fund Watchlist" },
  { file: "testAddCommodities.js",          label: "Commodity Watchlist" },
  { file: "testMockBondData.js",            label: "Bonds" }
];

const BATCH_SCRIPTS = [
  { file: "testRunSecuritySync.js", label: "Security Master Sync", waitAfter: 2000 },
  { file: "runBatch.js",            label: "Stock Data Load",      waitAfter: 3000 },
  { file: "testMFBatchRun.js",      label: "Mutual Fund NAV Load", waitAfter: 3000 },
  { file: "runCommodityBatch.js",   label: "Commodity Spot Load",  waitAfter: 1000 }
];

// ─────────────────────────────────────────────────────────────
// HELPERS
// ─────────────────────────────────────────────────────────────
const C = {
  reset: "\x1b[0m", bold: "\x1b[1m", dim: "\x1b[2m",
  green: "\x1b[32m", red: "\x1b[31m", yellow: "\x1b[33m",
  blue: "\x1b[34m", cyan: "\x1b[36m"
};

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

function banner(text) {
  const line = "─".repeat(62);
  console.log(`\n${C.cyan}${line}`);
  console.log(`  ${C.bold}${text}${C.reset}${C.cyan}`);
  console.log(`${line}${C.reset}\n`);
}

function runScript({ file, label }) {
  return new Promise((resolve) => {
    const filePath = path.resolve(process.cwd(), file);

    if (!existsSync(filePath)) {
      console.log(`${C.yellow}  SKIP${C.reset}  ${label}  ${C.dim}(${file} not found)${C.reset}\n`);
      return resolve({ file, label, status: "SKIPPED", ms: 0 });
    }

    console.log(`${C.blue}▶ RUN ${C.reset} ${C.bold}${label}${C.reset} ${C.dim}(${file})${C.reset}`);
    const started = Date.now();

    const child = spawn(process.execPath, [filePath], {
      stdio: "inherit",
      env: { ...process.env, BASE_URL }
    });

    child.on("close", (code) => {
      const ms = Date.now() - started;
      if (code === 0) {
        console.log(`${C.green}✔ DONE${C.reset} ${label} ${C.dim}(${ms} ms)${C.reset}\n`);
        resolve({ file, label, status: "SUCCESS", ms });
      } else {
        console.log(`${C.red}✖ FAIL${C.reset} ${label} ${C.dim}(exit ${code}, ${ms} ms)${C.reset}\n`);
        resolve({ file, label, status: "FAILED", ms, code });
      }
    });

    child.on("error", (err) => {
      const ms = Date.now() - started;
      console.log(`${C.red}✖ ERROR${C.reset} ${label}: ${err.message}\n`);
      resolve({ file, label, status: "ERROR", ms, error: err.message });
    });
  });
}

function printSummary(results, totalMs) {
  banner("SUMMARY");

  const pad = (s, n) => String(s).padEnd(n);
  console.log(`${C.bold}${pad("STEP", 28)}${pad("STATUS", 12)}TIME${C.reset}`);
  console.log("─".repeat(62));

  for (const r of results) {
    const colour =
      r.status === "SUCCESS" ? C.green :
      r.status === "SKIPPED" ? C.yellow : C.red;
    console.log(`${pad(r.label, 28)}${colour}${pad(r.status, 12)}${C.reset}${r.ms} ms`);
  }

  console.log("─".repeat(62));
  const ok      = results.filter((r) => r.status === "SUCCESS").length;
  const failed  = results.filter((r) => r.status === "FAILED" || r.status === "ERROR").length;
  const skipped = results.filter((r) => r.status === "SKIPPED").length;

  console.log(
    `${C.green}${ok} succeeded${C.reset}  ` +
    `${C.red}${failed} failed${C.reset}  ` +
    `${C.yellow}${skipped} skipped${C.reset}  ` +
    `${C.dim}total ${(totalMs / 1000).toFixed(1)}s${C.reset}\n`
  );

  return failed === 0;
}

// ─────────────────────────────────────────────────────────────
// MAIN
// ─────────────────────────────────────────────────────────────
async function main() {
  const args           = process.argv.slice(2);
  const seedOnly       = args.includes("--seed-only");
  const batchOnly      = args.includes("--batch-only");
  const continueOnFail = args.includes("--continue-on-error");

  const startedAt = Date.now();
  const results   = [];

  banner("UNIFIED SECURITY MASTER - FULL PIPELINE");

  // ---------- PHASE 1: SEED ----------
  if (!batchOnly) {
    banner("PHASE 1 / 2  ·  SEED REFERENCE DATA");
    for (const script of SEED_SCRIPTS) {
      const r = await runScript(script);
      results.push(r);
      if (r.status === "FAILED" && !continueOnFail) {
        console.error(`${C.red}Aborting - "${r.label}" failed.${C.reset}`);
        console.error(`${C.dim}Use --continue-on-error to keep going.${C.reset}`);
        printSummary(results, Date.now() - startedAt);
        process.exit(1);
      }
      await sleep(500);
    }
  }

  // ---------- PHASE 2: BATCH ----------
  if (!seedOnly) {
    banner("PHASE 2 / 2  ·  RUN BATCH JOBS");
    for (const script of BATCH_SCRIPTS) {
      const r = await runScript(script);
      results.push(r);
      if (r.status === "FAILED" && !continueOnFail) {
        console.error(`${C.red}Aborting - "${r.label}" failed.${C.reset}`);
        printSummary(results, Date.now() - startedAt);
        process.exit(1);
      }
      if (script.waitAfter) {
        console.log(`${C.dim}  waiting ${script.waitAfter} ms ...${C.reset}`);
        await sleep(script.waitAfter);
      }
    }
  }

  const allOk = printSummary(results, Date.now() - startedAt);
  process.exit(allOk ? 0 : 1);
}

main().catch((err) => {
  console.error(`${C.red}Fatal: ${err.message}${C.reset}`);
  process.exit(1);
});