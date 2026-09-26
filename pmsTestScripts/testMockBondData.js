import axios from 'axios'

const createBond = async (bond) => {
    const res = await axios.post("http://localhost:8081/api/bonds/add-bond",bond);
    console.log(res);
}

// -------------------------------------------------------------------
// Bonds - Indian NSE, all Corporate -> assetId 17 (Corporation Bond)
// -------------------------------------------------------------------

const infosysBond = {
  assetId:         17,               // Cor poration Bond
  isin:            "INE009A01241",
  name:            "Infosys Limited 7.85% 2029",
  issuerName:      "Infosys Tech Ltd",
  bondType:        "Corporate",
  exchange:        "NSE",
  currency:        "INR",
  faceValue:       1000.00,
  couponRate:      0.0785,
  couponFrequency: "SemiAnnual",
  issueDate:       "2019-05-20",
  maturityDate:    "2029-05-20",
  creditRating:    "A-"
};

const tcsBond = {
  assetId:         17,
  isin:            "INE467B01211",
  name:            "Tata Consultancy 7.60% 2028",
  issuerName:      "Tata Consultancy Ltd",
  bondType:        "Corporate",
  exchange:        "NSE",
  currency:        "INR",
  faceValue:       1000.00,
  couponRate:      0.0760,
  couponFrequency: "SemiAnnual",
  issueDate:       "2018-08-10",
  maturityDate:    "2028-08-10",
  creditRating:    "A-"
};

const wiproBond = {
  assetId:         17,
  isin:            "INE075A01332",
  name:            "Wipro Limited 7.20% 2027",
  issuerName:      "Wipro Ltd",
  bondType:        "Corporate",
  exchange:        "NSE",
  currency:        "INR",
  faceValue:       1000.00,
  couponRate:      0.0720,
  couponFrequency: "Annual",
  issueDate:       "2017-03-15",
  maturityDate:    "2027-03-15",
  creditRating:    "BBB+"
};

const hclTechBond = {
  assetId:         17,
  isin:            "INE860A01418",
  name:            "HCL Technologies 8.10% 2030",
  issuerName:      "HCL Technologies Ltd",
  bondType:        "Corporate",
  exchange:        "NSE",
  currency:        "INR",
  faceValue:       1000.00,
  couponRate:      0.0810,
  couponFrequency: "SemiAnnual",
  issueDate:       "2020-06-01",
  maturityDate:    "2030-06-01",
  creditRating:    "A-"
};

const techMahindraBond = {
  assetId:         17,
  isin:            "INE669C01325",
  name:            "Tech Mahindra 7.45% 2026",
  issuerName:      "Tech Mahindra Ltd",
  bondType:        "Corporate",
  exchange:        "NSE",
  currency:        "INR",
  faceValue:       1000.00,
  couponRate:      0.0745,
  couponFrequency: "SemiAnnual",
  issueDate:       "2016-11-20",
  maturityDate:    "2026-11-20",
  creditRating:    "BBB+"
};

const ltiMindtreeBond = {
  assetId:         17,
  isin:            "INE214T01127",
  name:            "LTIM Limited 6.95% 2027",
  issuerName:      "LTIM Limited",
  bondType:        "Corporate",
  exchange:        "NSE",
  currency:        "INR",
  faceValue:       1000.00,
  couponRate:      0.0695,
  couponFrequency: "Annual",
  issueDate:       "2017-09-12",
  maturityDate:    "2027-09-12",
  creditRating:    "BBB"
};

const persistentBond = {
  assetId:         17,
  isin:            "INE262H01234",
  name:            "Persistent Systems 7.30% 2028",
  issuerName:      "Persistent Systems Ltd",
  bondType:        "Corporate",
  exchange:        "NSE",
  currency:        "INR",
  faceValue:       1000.00,
  couponRate:      0.0730,
  couponFrequency: "SemiAnnual",
  issueDate:       "2018-04-05",
  maturityDate:    "2028-04-05",
  creditRating:    "BBB+"
};

const coforgeBond = {
  assetId:         17,
  isin:            "INE591G01129",
  name:            "Coforge Limited 8.40% 2031",
  issuerName:      "Coforge Ltd",
  bondType:        "Corporate",
  exchange:        "NSE",
  currency:        "INR",
  faceValue:       1000.00,
  couponRate:      0.0840,
  couponFrequency: "SemiAnnual",
  issueDate:       "2021-02-18",
  maturityDate:    "2031-02-18",
  creditRating:    "A-"
};

const mphasisBond = {
  assetId:         17,
  isin:            "INE356A01133",
  name:            "Mphasis Limited 7.55% 2029",
  issuerName:      "Mphasis Ltd",
  bondType:        "Corporate",
  exchange:        "NSE",
  currency:        "INR",
  faceValue:       1000.00,
  couponRate:      0.0755,
  couponFrequency: "Annual",
  issueDate:       "2019-07-25",
  maturityDate:    "2029-07-25",
  creditRating:    "BBB"
};

const oracleFinServBond = {
  assetId:         17,
  isin:            "INE881D01231",
  name:            "Oracle Financial Services 7.05% 2028",
  issuerName:      "Oracle Financial Services Ltd",
  bondType:        "Corporate",
  exchange:        "NSE",
  currency:        "INR",
  faceValue:       1000.00,
  couponRate:      0.0705,
  couponFrequency: "SemiAnnual",
  issueDate:       "2018-10-30",
  maturityDate:    "2028-10-30",
  creditRating:    "A-"
};

const ltTechServicesBond = {
  assetId:         17,
  isin:            "INE010V01235",
  name:            "L&T Technology Services 7.75% 2030",
  issuerName:      "L&T Technology Services Ltd",
  bondType:        "Corporate",
  exchange:        "NSE",
  currency:        "INR",
  faceValue:       1000.00,
  couponRate:      0.0775,
  couponFrequency: "SemiAnnual",
  issueDate:       "2020-01-15",
  maturityDate:    "2030-01-15",
  creditRating:    "A-"
};

const tataElxsiBond = {
  assetId:         17,
  isin:            "INE670A01337",
  name:            "Tata Elxsi Limited 6.85% 2027",
  issuerName:      "Tata Elxsi Ltd",
  bondType:        "Corporate",
  exchange:        "NSE",
  currency:        "INR",
  faceValue:       1000.00,
  couponRate:      0.0685,
  couponFrequency: "Annual",
  issueDate:       "2017-06-10",
  maturityDate:    "2027-06-10",
  creditRating:    "BBB+"
};

const kpitTechBond = {
  assetId:         17,
  isin:            "INE04I401135",
  name:            "KPIT Technologies 8.25% 2029",
  issuerName:      "KPIT Technologies Ltd",
  bondType:        "Corporate",
  exchange:        "NSE",
  currency:        "INR",
  faceValue:       1000.00,
  couponRate:      0.0825,
  couponFrequency: "SemiAnnual",
  issueDate:       "2019-12-01",
  maturityDate:    "2029-12-01",
  creditRating:    "A-"
};

const cyientBond = {
  assetId:         17,
  isin:            "INE136B01139",
  name:            "Cyient Limited 7.40% 2028",
  issuerName:      "Cyient Ltd",
  bondType:        "Corporate",
  exchange:        "NSE",
  currency:        "INR",
  faceValue:       1000.00,
  couponRate:      0.0740,
  couponFrequency: "Annual",
  issueDate:       "2018-03-22",
  maturityDate:    "2028-03-22",
  creditRating:    "BBB"
};

const birlasoftBond = {
  assetId:         17,
  isin:            "INE836A01332",
  name:            "Birlasoft Limited 7.90% 2031",
  issuerName:      "Birlasoft Ltd",
  bondType:        "Corporate",
  exchange:        "NSE",
  currency:        "INR",
  faceValue:       1000.00,
  couponRate:      0.0790,
  couponFrequency: "SemiAnnual",
  issueDate:       "2021-05-10",
  maturityDate:    "2031-05-10",
  creditRating:    "A-"
};

const bonds = [
  infosysBond, tcsBond, wiproBond, hclTechBond, techMahindraBond,
  ltiMindtreeBond, persistentBond, coforgeBond, mphasisBond, oracleFinServBond,
  ltTechServicesBond, tataElxsiBond, kpitTechBond, cyientBond, birlasoftBond
];

for (let bond of bonds) {
    bond = {...bond,country:"IN"}
    await createBond(bond);
}