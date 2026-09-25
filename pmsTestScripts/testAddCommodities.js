import axios from "axios";

const gold = {
  productId: "10001",
  symbol:    "GOLD",
  name:      "Gold Spot",
  quotation: "10 Grams",
  unit:      "1 Kg",
  exchange:  "NSE",
  assetId:   20,          
  status:    true
};

const silver = {
  productId: "10002",
  symbol:    "SILVER",
  name:      "Silver Spot",
  quotation: "1 Kg",
  unit:      "30 Kg",
  exchange:  "NSE",
  assetId:   20,
  status:    true
};

const commodities = [gold];

const addCommodity = async (commodity)=>{
    const res = await axios.post("http://localhost:8081/api/commodity-watchlist/add-commodity",commodity);
    console.log(res.data)
}

for(const commodity of commodities){
    await addCommodity(commodity);
}