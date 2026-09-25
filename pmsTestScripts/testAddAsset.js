import axios from 'axios';

const addAsset = async (asset) =>{
    try{
        const res = await axios.post("http://localhost:8081/api/assets/add-asset",asset);
        console.log(res.data)
        return res.data
    }catch(e){
        return e.response;
    }
}


const cash = {
  assetClass:          "Cash",
  description:         "Cash asset class",
  assetSubclass:       "Cash",
  risk:                "LOW",
  investmentHorizon:   "SHORT",
  subAssetDescription: "Cash balance in Bank account",
  status:              true
};


const equityStock = {
  assetClass:          "Equity",
  description:         "Equity asset class",
  assetSubclass:       "Stock",
  risk:                "HIGH",
  investmentHorizon:   "LONG",
  subAssetDescription: "Listed Equity stocks in Exchanges",
  status:              true
};


const mfStockFunds = {
  assetClass:          "Mutual Fund",
  description:         "Mutual fund asset class",
  assetSubclass:       "Stock Funds",
  risk:                "HIGH",
  investmentHorizon:   "LONG",
  subAssetDescription: "Stock mutual fund invests principally in equity or stocks",
  status:              true
};

const mfBondFunds = {
  assetClass:          "Mutual Fund",
  description:         "Mutual fund asset class",
  assetSubclass:       "Bond Funds",
  risk:                "LOW",
  investmentHorizon:   "ANY",
  subAssetDescription: "Fixed-income mutual fund focuses on investments that pay a set rate of return, such as government bonds, corporate bonds, or other debt instruments",
  status:              true
};

const mfIndexFunds = {
  assetClass:          "Mutual Fund",
  description:         "Mutual fund asset class",
  assetSubclass:       "Index Funds",
  risk:                "MEDIUM",
  investmentHorizon:   "ANY",
  subAssetDescription: "Index Funds invest in stocks that correspond with a major market index such as the S&P 500 or the Dow Jones Industrial Average (DJIA)",
  status:              true
};

const mfBalancedFunds = {
  assetClass:          "Mutual Fund",
  description:         "Mutual fund asset class",
  assetSubclass:       "Balanced Funds",
  risk:                "MEDIUM",
  investmentHorizon:   "LONG",
  subAssetDescription: "Balanced funds invest in a hybrid of asset classes, whether stocks, bonds, money market instruments, or alternative investments",
  status:              true
};

const mfMoneyMarketFunds = {
  assetClass:          "Mutual Fund",
  description:         "Mutual fund asset class",
  assetSubclass:       "Money Market Funds",
  risk:                "LOW",
  investmentHorizon:   "SHORT",
  subAssetDescription: "The money market consists of safe, risk-free, short-term debt instruments, mostly government Treasury bills",
  status:              true
};

const mfIncomeFunds = {
  assetClass:          "Mutual Fund",
  description:         "Mutual fund asset class",
  assetSubclass:       "Income Funds",
  risk:                "HIGH",
  investmentHorizon:   "LONG",
  subAssetDescription: "Income funds are named for their purpose: to provide current income on a steady basis. These funds invest primarily in government and high quality corporate debt, holding these bonds until maturity to provide interest streams",
  status:              true
};

const mfInternationalFunds = {
  assetClass:          "Mutual Fund",
  description:         "Mutual fund asset class",
  assetSubclass:       "International/Global Funds",
  risk:                "HIGH",
  investmentHorizon:   "LONG",
  subAssetDescription: "An international fund, or foreign fund, invests only in assets located outside an investor's home country",
  status:              true
};

const mfSpecialityFunds = {
  assetClass:          "Mutual Fund",
  description:         "Mutual fund asset class",
  assetSubclass:       "Speciality Funds",
  risk:                "HIGH",
  investmentHorizon:   "LONG",
  subAssetDescription: "Sector funds are targeted strategy funds aimed at specific sectors of the economy, such as financial, technology, or healthcare",
  status:              true
};

const mfEtfs = {
  assetClass:          "Mutual Fund",
  description:         "Mutual fund asset class",
  assetSubclass:       "Exchange Traded Funds (ETFs)",
  risk:                "MEDIUM",
  investmentHorizon:   "ANY",
  subAssetDescription: "ETFs are not considered mutual funds but employ strategies consistent with mutual funds",
  status:              true
};


const fiTBills = {
  assetClass:          "Fixed Income",
  description:         "Fixed Income asset class",
  assetSubclass:       "T-Bills (Treasury Bills)",
  risk:                "LOW",
  investmentHorizon:   "SHORT",
  subAssetDescription: "T-bills are short-term fixed-income securities that mature within one year that do not pay coupon returns",
  status:              true
};

const fiTNotes = {
  assetClass:          "Fixed Income",
  description:         "Fixed Income asset class",
  assetSubclass:       "T-Notes (Treasury Notes)",
  risk:                "LOW",
  investmentHorizon:   "ANY",
  subAssetDescription: "Treasury notes (T-notes) come in maturities between two and 10 years, pay a fixed interest rate, and are sold in multiples of $100",
  status:              true
};

const fiTBonds = {
  assetClass:          "Fixed Income",
  description:         "Fixed Income asset class",
  assetSubclass:       "T-Bonds (Treasury Bonds)",
  risk:                "LOW",
  investmentHorizon:   "LONG",
  subAssetDescription: "Treasury bonds (T-bonds) are similar to the T-note except that it matures in 20 or 30 years",
  status:              true
};

const fiTips = {
  assetClass:          "Fixed Income",
  description:         "Fixed Income asset class",
  assetSubclass:       "TIPS (Treasury Inflation-Protected Securities)",
  risk:                "LOW",
  investmentHorizon:   "LONG",
  subAssetDescription: "Treasury Inflation-Protected Securities (TIPS) protect investors from inflation. The principal amount of a TIPS bond adjusts with inflation and deflation. Sold as 5, 10 or 30 years maturity",
  status:              true
};

const fiMunicipalBond = {
  assetClass:          "Fixed Income",
  description:         "Fixed Income asset class",
  assetSubclass:       "Municipal Bond",
  risk:                "LOW",
  investmentHorizon:   "LONG",
  subAssetDescription: "Municipal Bond is similar to a Treasury since it is government-issued, except it is issued and backed by a state, municipality, or county",
  status:              true
};

const fiCorporateBond = {
  assetClass:          "Fixed Income",
  description:         "Fixed Income asset class",
  assetSubclass:       "Corporation Bond",
  risk:                "MEDIUM",
  investmentHorizon:   "MEDIUM",
  subAssetDescription: "Corporate bonds come in various types, and the price and interest rate offered largely depend on the company's financial stability and its creditworthiness. Bonds with higher credit ratings typically pay lower coupon rates",
  status:              true
};

const fiJunkBond = {
  assetClass:          "Fixed Income",
  description:         "Fixed Income asset class",
  assetSubclass:       "Junk Bond",
  risk:                "HIGH",
  investmentHorizon:   "MEDIUM",
  subAssetDescription: "Junk bonds, also called high-yield bonds, are corporate issues that pay a greater coupon due to the higher risk of default",
  status:              true
};

const fiCertificateOfDeposit = {
  assetClass:          "Fixed Income",
  description:         "Fixed Income asset class",
  assetSubclass:       "Certificate of Deposit (CD)",
  risk:                "MEDIUM",
  investmentHorizon:   "MEDIUM",
  subAssetDescription: "A certificate of deposit (CD) is a fixed income vehicle offered by financial institutions with maturities of less than five years",
  status:              true
};

const commoditiesGold = {
  assetClass:          "Commodities",
  description:         "Commodities asset class",
  assetSubclass:       "Gold",
  risk:                "LOW",
  investmentHorizon:   "LONG",
  subAssetDescription: "24 Ct Gold traded in Exchanges",
  status:              true
};


const reits = {
  assetClass:          "REITs",
  description:         "Real Estate Investment Trusts",
  assetSubclass:       "REITs",
  risk:                "MEDIUM",
  investmentHorizon:   "LONG",
  subAssetDescription: "Real estate investment trusts. Typically earn income through rental income for investments made in properties",
  status:              true
};

const equitlETF = {
  assetClass:          "Equity ETF",
  description:         "Equity Exchange Traded Fund asset class",
  assetSubclass:       "Index ETF",
  risk:                "HIGH",
  investmentHorizon:   "LONG",
  subAssetDescription: "Index-tracking ETFs listed on Exchanges (Nifty 50, Nifty Bank, Midcap 150, etc.)",
  status:              true
};


const debtETF = {
  assetClass:          "Debt ETF",
  description:         "Debt Exchange Traded Fund asset class",
  assetSubclass:       "G-Sec ETF",
  risk:                "LOW",
  investmentHorizon:   "SHORT_TO_MEDIUM",
  subAssetDescription: "G-Sec / money market ETFs listed on Exchanges (Nifty 8-13 yr G-Sec, Liquid, etc.)",
  status:              true
};


const comodityETF = {
  assetClass:          "Commodity ETF",
  description:         "Commodity Exchange Traded Fund asset class",
  assetSubclass:       "Physical ETF",
  risk:                "MEDIUM",
  investmentHorizon:   "LONG",
  subAssetDescription: "Physical-backed ETFs listed on Exchanges (Gold BeES, Silver BeES)",
  status:              true
};
const assets = [
  cash,
  equityStock,
  mfStockFunds,
  mfBondFunds,
  mfIndexFunds,
  mfBalancedFunds,
  mfMoneyMarketFunds,
  mfIncomeFunds,
  mfInternationalFunds,
  mfSpecialityFunds,
  mfEtfs,
  fiTBills,
  fiTNotes,
  fiTBonds,
  fiTips,
  fiMunicipalBond,
  fiCorporateBond,
  fiJunkBond,
  fiCertificateOfDeposit,
  commoditiesGold,
  reits,
  equitlETF,
  debtETF,
  comodityETF
];

for(const asset of assets){
    await addAsset(asset);
}