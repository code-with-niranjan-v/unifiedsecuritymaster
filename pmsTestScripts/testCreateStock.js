import axios from 'axios'
const createStock = async (stock)=>{
    const res = await axios.post("http://localhost:8081/api/stock-watchlist/add-stock",stock);
    console.log(res);
}

const infosys = {
  symbol:   "INFY",
  name:     "Infosys Limited",
  exchange: "NSE",
  isin:     "INE009A01021",
  gics:     "45102010",
  country:  "India",
  industry: "IT Consulting & Other Services",
  sector:   "Information Technology",
  assetId: 2
};

const tcs = {
  symbol:   "TCS",
  name:     "Tata Consultancy Services Limited",
  exchange: "NSE",
  isin:     "INE467B01029",
  gics:     "45102010",
  country:  "India",
  industry: "IT Consulting & Other Services",
  sector:   "Information Technology",
   assetId: 2
};

const wipro = {
  symbol:   "WIPRO",
  name:     "Wipro Limited",
  exchange: "NSE",
  isin:     "INE075A01022",
  gics:     "45102010",
  country:  "India",
  industry: "IT Consulting & Other Services",
  sector:   "Information Technology",
   assetId: 2
};

const hclTech = {
  symbol:   "HCLTECH",
  name:     "HCL Technologies Limited",
  exchange: "NSE",
  isin:     "INE860A01027",
  gics:     "45102010",
  country:  "India",
  industry: "IT Consulting & Other Services",
  sector:   "Information Technology"
  , assetId: 2
};

const techMahindra = {
  symbol:   "TECHM",
  name:     "Tech Mahindra Limited",
  exchange: "NSE",
  isin:     "INE669C01036",
  gics:     "45102010",
  country:  "India",
  industry: "IT Consulting & Other Services",
  sector:   "Information Technology",
   assetId: 2
};

const ltiMindtree = {
  symbol:   "LTIM",
  name:     "LTIMindtree Limited",
  exchange: "NSE",
  isin:     "INE214T01019",
  gics:     "45102010",
  country:  "India",
  industry: "IT Consulting & Other Services",
  sector:   "Information Technology",
   assetId: 2
};

const persistent = {
  symbol:   "PERSISTENT",
  name:     "Persistent Systems Limited",
  exchange: "NSE",
  isin:     "INE262H01021",
  gics:     "45102010",
  country:  "India",
  industry: "IT Consulting & Other Services",
  sector:   "Information Technology",
   assetId: 2
};

const coforge = {
  symbol:   "COFORGE",
  name:     "Coforge Limited",
  exchange: "NSE",
  isin:     "INE591G01017",
  gics:     "45102010",
  country:  "India",
  industry: "IT Consulting & Other Services",
  sector:   "Information Technology",
   assetId: 2
};

const mphasis = {
  symbol:   "MPHASIS",
  name:     "Mphasis Limited",
  exchange: "NSE",
  isin:     "INE356A01018",
  gics:     "45102010",
  country:  "India",
  industry: "IT Consulting & Other Services",
  sector:   "Information Technology",
   assetId: 2
};

const oracleFinServ = {
  symbol:   "OFSS",
  name:     "Oracle Financial Services Software Limited",
  exchange: "NSE",
  isin:     "INE881D01027",
  gics:     "45103010",
  country:  "India",
  industry: "Application Software",
  sector:   "Information Technology",
   assetId: 2
};

const ltTechServices = {
  symbol:   "LTTS",
  name:     "L&T Technology Services Limited",
  exchange: "NSE",
  isin:     "INE010V01017",
  gics:     "45102010",
  country:  "India",
  industry: "IT Consulting & Other Services",
  sector:   "Information Technology",
   assetId: 2
};

const tataElxsi = {
  symbol:   "TATAELXSI",
  name:     "Tata Elxsi Limited",
  exchange: "NSE",
  isin:     "INE670A01012",
  gics:     "45102010",
  country:  "India",
  industry: "IT Consulting & Other Services",
  sector:   "Information Technology",
   assetId: 2
};

const kpitTech = {
  symbol:   "KPITTECH",
  name:     "KPIT Technologies Limited",
  exchange: "NSE",
  isin:     "INE04I401011",
  gics:     "45102010",
  country:  "India",
  industry: "IT Consulting & Other Services",
  sector:   "Information Technology",
   assetId: 2
};

const cyient = {
  symbol:   "CYIENT",
  name:     "Cyient Limited",
  exchange: "NSE",
  isin:     "INE136B01020",
  gics:     "45102010",
  country:  "India",
  industry: "IT Consulting & Other Services",
  sector:   "Information Technology",
   assetId: 2
};

const birlasoft = {
  symbol:   "BSOFT",
  name:     "Birlasoft Limited",
  exchange: "NSE",
  isin:     "INE836A01035",
  gics:     "45102010",
  country:  "India",
  industry: "IT Consulting & Other Services",
  sector:   "Information Technology",
   assetId: 2
};

const stocks = [
  infosys, tcs, wipro, hclTech, techMahindra,
  ltiMindtree, persistent, coforge, mphasis, oracleFinServ,
  ltTechServices, tataElxsi, kpitTech, cyient, birlasoft
];
const etfs = [
  {
    symbol:   "NIFTYBEES",
    name:     "Nippon India ETF Nifty 50 BeES",
    exchange: "NSE",
    isin:     "INF204KB14I2",
    gics:     "40203010",   // inference: Financials (open-ended index scheme) — not officially published
    country:  "India",
    industry: "Exchange Traded Fund",
    sector:   "Financial Services",
     assetId: 22
  },
  {
    symbol:   "BANKBEES",
    name:     "Nippon India ETF Nifty Bank BeES",
    exchange: "NSE",
    isin:     "INF204KB15I9",         // not disclosed on retrieved pages — needs verification
    gics:     "40203010",   // inference: Financials
    country:  "India",
    industry: "Exchange Traded Fund",
    sector:   "Financial Services",
     assetId: 22
  },
  {
    symbol:   "MID150BEES",
    name:     "Nippon India ETF Nifty Midcap 150",
    exchange: "NSE",
    isin:     "INF204KB1V68",
    gics:     "40203010",   // inference: Financials
    country:  "India",
    industry: "Exchange Traded Fund",
    sector:   "Financial Services",
     assetId: 22
  },
  {
    symbol:   "GOLDBEES",
    name:     "Nippon India ETF Gold BeES",
    exchange: "NSE",
    isin:     "INF204KB17I5",         // not disclosed on retrieved pages — needs verification
    gics:     "40203010",   // inference: Financials (commodity ETF)
    country:  "India",
    industry: "Exchange Traded Fund",
    sector:   "Commodity",
     assetId: 24
  },
  {
    symbol:   "SILVERBEES",
    name:     "Nippon India ETF Silver BeES",
    exchange: "NSE",
    isin:     "INF204KC1402",         // not disclosed on retrieved pages — needs verification
    gics:     "40203010",   // inference: Financials (commodity ETF)
    country:  "India",
    industry: "Exchange Traded Fund",
    sector:   "Commodity",
     assetId: 24
  }
];
for(const stock of stocks){
    await createStock(stock);
}

for(const etf of etfs){
  await createStock(etf);
}