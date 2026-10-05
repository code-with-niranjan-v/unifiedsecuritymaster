import { useEffect, useState } from "react";
import api from "../api";
import Input from "../components/Input";
import Select from "../components/Select";

function Watchlists() {
  const [activeTab, setActiveTab] = useState("stocks");

  const [stocks, setStocks] = useState([]);
  const [mutualFunds, setMutualFunds] =  useState([]);
  const [commodities, setCommodities] = useState([]);

  const [stockForm, setStockForm] = useState({
    symbol: "",
    name: "",
    exchange: "",
    isin: "",
    gics: "",
    country: "",
    industry: "",
    sector: "",
    assetId: "",
    equityCategory: ""
  });

  const [mutualFundForm, setMutualFundForm] = useState({
    isin: "",
    schemeName: "",
    assetId: "",
    equityCategory: ""
  });

  const [commodityForm, setCommodityForm] = useState({
    productId: "",
    symbol: "",
    name: "",
    quotation: "",
    unit: "",
    exchange: "",
    assetId: "",
    status: true
  });

  const [stockDeleteId, setStockDeleteId] = useState("");
  const [mutualFundDeleteId, setMutualFundDeleteId] =
    useState("");
  const [commodityDeleteId, setCommodityDeleteId] =
    useState("");

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() =>{

    fetchStocks();
    fetchMutualFunds();
    fetchCommodities();
  }, []);

  const fetchStocks = async () =>{
    try{
      const response = await api.get("/stock-watchlist/all-stocks");

      setStocks(response.data?.data || []);

    }catch(error){

      setError(error.response?.data?.message || "Unable to load stocks");
    }
  };


  const fetchMutualFunds = async () =>{
    try{
      const response = await api.get("/mutualfunds-watchlist/all-mutual-funds");

      setMutualFunds(response.data?.data || []);

    }catch(error){

      setError(error.response?.data?.message || "Unable to load mutual funds");
    }
  };

  const fetchCommodities = async () =>{
    try{
      const response = await api.get("/commodity-watchlist/all-commodities");

      setCommodities(response.data?.data || []);

    }catch(error){

      setError(error.response?.data?.message || "Unable to load commodities");
    }
  };

  const showSuccess = (text) => {
    setError("");
    setMessage(text);
  };

  const showError = (text) => {
    setMessage("");
    setError(text);
  };

  const addStock = async (e) => {
    e.preventDefault();

    try {
      const payload = {
        ...stockForm,
        assetId: Number(stockForm.assetId)
      };

      await api.post(
        "/stock-watchlist/add-stock",
        payload
      );

      showSuccess("Stock added to watchlist.");

      setStockForm({
        symbol: "",
        name: "",
        exchange: "",
        isin: "",
        gics: "",
        country: "",
        industry: "",
        sector: "",
        assetId: "",
        equityCategory: ""
      });

      await fetchStocks();

    } catch (err) {
      showError(
        err.response?.data?.message ||
          "Unable to add stock."
      );
    }
  };

  const deleteStock = async () => {
    if (!stockDeleteId) {
      showError("Enter the stock watchlist ID.");
      return;
    }

    try {
      await api.delete(
        `/stock-watchlist/delete-stock/${stockDeleteId}`
      );

      showSuccess("Stock removed from watchlist.");
      setStockDeleteId("");

      await fetchStocks();

    } catch (err) {
      showError(
        err.response?.data?.message ||
          "Unable to delete stock."
      );
    }
  };

  const addMutualFund = async (e) => {
    e.preventDefault();

    try {
      const payload = {
        ...mutualFundForm,
        assetId: Number(mutualFundForm.assetId)
      };

      await api.post(
        "/mutualfunds-watchlist/add-mutual-fund",
        payload
      );

      showSuccess("Mutual fund added to watchlist.");

      setMutualFundForm({
        isin: "",
        schemeName: "",
        assetId: "",
        equityCategory: ""
      });

      await fetchMutualFunds();

    } catch (err) {
      showError(
        err.response?.data?.message ||
          "Unable to add mutual fund."
      );
    }
  };

  const deleteMutualFund = async () => {
    if (!mutualFundDeleteId) {
      showError("Enter the mutual fund watchlist ID.");
      return;
    }

    try {
      await api.delete(
        `/mutualfunds-watchlist/delete-mutual-fund/${mutualFundDeleteId}`
      );

      showSuccess("Mutual fund removed from watchlist.");
      setMutualFundDeleteId("");

      await fetchMutualFunds();

    } catch (err) {
      showError(
        err.response?.data?.message ||
          "Unable to delete mutual fund."
      );
    }
  };

  const addCommodity = async (e) => {
    e.preventDefault();

    try {
      const payload = {
        ...commodityForm,
        assetId: Number(commodityForm.assetId),
        status: Boolean(commodityForm.status)
      };

      await api.post(
        "/commodity-watchlist/add-commodity",
        payload
      );

      showSuccess("Commodity added to watchlist.");

      setCommodityForm({
        productId: "",
        symbol: "",
        name: "",
        quotation: "",
        unit: "",
        exchange: "",
        assetId: "",
        status: true
      });

      await fetchCommodities();

    } catch (err) {
      showError(
        err.response?.data?.message ||
          "Unable to add commodity."
      );
    }
  };

  const deleteCommodity = async () => {
    if (!commodityDeleteId) {
      showError("Enter the commodity watchlist ID.");
      return;
    }

    try {
      await api.delete(
        `/commodity-watchlist/delete-commodity/${commodityDeleteId}`
      );

      showSuccess("Commodity removed from watchlist.");
      setCommodityDeleteId("");

      await fetchCommodities();

    } catch (err) {
      showError(
        err.response?.data?.message ||
          "Unable to delete commodity."
      );
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Watchlists</h1>
          <p>
            Manage stocks, mutual funds and commodities.
          </p>
        </div>
      </div>

      {message && <div className="success-message">{message}</div>}
      {error && <div className="error-message">{error}</div>}

      <div className="tabs">
        <button
          className={activeTab === "stocks" ? "tab active" : "tab"}
          onClick={() => setActiveTab("stocks")}
        >
          Stocks
        </button>

        <button
          className={
            activeTab === "mutualFunds"
              ? "tab active"
              : "tab"
          }
          onClick={() => setActiveTab("mutualFunds")}
        >
          Mutual Funds
        </button>

        <button
          className={
            activeTab === "commodities"
              ? "tab active"
              : "tab"
          }
          onClick={() => setActiveTab("commodities")}
        >
          Commodities
        </button>
      </div>

      {activeTab === "stocks" && (
        <div className="two-column">
          <div className="card">
            <h2>Add Stock</h2>

            <form onSubmit={addStock}>
              <Input
                label="Symbol"
                value={stockForm.symbol}
                onChange={(value) =>
                  setStockForm({
                    ...stockForm,
                    symbol: value
                  })
                }
                required
              />

              <Input
                label="Name"
                value={stockForm.name}
                onChange={(value) =>
                  setStockForm({
                    ...stockForm,
                    name: value
                  })
                }
                required
              />

              <Select
                label="Exchange"
                value={stockForm.exchange}
                onChange={(value) =>
                  setStockForm({
                    ...stockForm,
                    exchange: value
                  })
                }
                options={[
                  "NSE",
                  "LSE",
                  "NasdaQ"
                ]}
              />

              <Input
                label="ISIN"
                value={stockForm.isin}
                onChange={(value) =>
                  setStockForm({
                    ...stockForm,
                    isin: value
                  })
                }
              />

              <Input
                label="GICS"
                value={stockForm.gics}
                onChange={(value) =>
                  setStockForm({
                    ...stockForm,
                    gics: value
                  })
                }
              />

              <Input
                label="Country"
                value={stockForm.country}
                onChange={(value) =>
                  setStockForm({
                    ...stockForm,
                    country: value
                  })
                }
              />

              <Input
                label="Industry"
                value={stockForm.industry}
                onChange={(value) =>
                  setStockForm({
                    ...stockForm,
                    industry: value
                  })
                }
              />

              <Input
                label="Sector"
                value={stockForm.sector}
                onChange={(value) =>
                  setStockForm({
                    ...stockForm,
                    sector: value
                  })
                }
              />

              <Select label="Equity Category" value={stockForm.equityCategory} onChange={(value) => setStockForm({ ...stockForm, equityCategory: value })} options={["SMALL_CAP", "MID_CAP", "LARGE_CAP"]} required />

              <Input
                label="Asset ID"
                type="number"
                value={stockForm.assetId}
                onChange={(value) =>
                  setStockForm({
                    ...stockForm,
                    assetId: value
                  })
                }
                required
              />

              <button
                className="primary-button"
                type="submit"
              >
                Add Stock
              </button>
            </form>
          </div>

          <div className="card">
            <h2>Stocks</h2>

            {stocks.length > 0 ? (
              <div className="table-wrapper">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Symbol</th>
                      <th>Name</th>
                      <th>Exchange</th>
                      <th>ISIN</th>
                      <th>GICS</th>
                      <th>Country</th>
                      <th>Industry</th>
                      <th>Sector</th>
                      <th>Equity Category</th>
                      <th>Asset ID</th>
                      <th>Action</th>
                    </tr>
                  </thead>

                  <tbody>
                    {stocks.map( (stock) =>(
                      <tr key = {stock.id}>
                        <td>{stock.id}</td>
                        <td>{stock.symbol}</td>
                        <td>{stock.name}</td>
                        <td>{stock.exchange}</td>
                        <td>{stock.isin}</td>
                        <td>{stock.gics}</td>
                        <td>{stock.country}</td>
                        <td>{stock.industry}</td>
                        <td>{stock.sector}</td>
                        <td>{stock.equityCategory || "-"}</td>
                        <td>{stock.asset?.id ?? "-"}</td>
                        <td>
                          <button className="danger-button small-button"
                                  onClick = {() => deleteStock(stock.id)}
                          >
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <p className="empty-message">
                No stocks in the watchlist
              </p>
            )}
          </div>
          
          

          <div className="card delete-section">
            <h2>Delete Stock by ID</h2>

            <Input
              label="Stock Watchlist ID"
              type="number"
              value={stockDeleteId}
              onChange={setStockDeleteId}
            />

            <button
              className="danger-button"
              onClick={deleteStock}
            >
              Delete Stock
            </button>
          </div>
        </div>
      )}

      {activeTab === "mutualFunds" && (
        <div className="two-column">
          <div className="card">
            <h2>Add Mutual Fund</h2>

            <form onSubmit={addMutualFund}>
              <Input
                label="ISIN"
                value={mutualFundForm.isin}
                onChange={(value) =>
                  setMutualFundForm({
                    ...mutualFundForm,
                    isin: value
                  })
                }
                required
              />

              <Input
                label="Scheme Name"
                value={mutualFundForm.schemeName}
                onChange={(value) =>
                  setMutualFundForm({
                    ...mutualFundForm,
                    schemeName: value
                  })
                }
                required
              />

              <Select label="Equity Category" value={mutualFundForm.equityCategory} onChange={(value) => setMutualFundForm({ ...mutualFundForm, equityCategory: value })} options={["SMALL_CAP", "MID_CAP", "LARGE_CAP"]} required />

              <Input
                label="Asset ID"
                type="number"
                value={mutualFundForm.assetId}
                onChange={(value) =>
                  setMutualFundForm({
                    ...mutualFundForm,
                    assetId: value
                  })
                }
                required
              />

              <button
                className="primary-button"
                type="submit"
              >
                Add Mutual Fund
              </button>
            </form>
          </div>

          <div className="card">
            <h2>Mutual Funds</h2>

            {mutualFunds.length > 0 ? (
              <div className="table-wrapper">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>ISIN</th>
                      <th>Scheme Name</th>
                      <th>Equity Category</th>
                      <th>Asset ID</th>
                      <th>Status</th>
                      <th>Last Updated</th>
                      <th>Action</th>
                    </tr>
                  </thead>

                  <tbody>
                    {mutualFunds.map( (mf) =>(
                      <tr key = {mf.id}>
                        <td>{mf.id}</td>
                        <td>{mf.isin}</td>
                        <td>{mf.schemeName}</td>
                        <td>{mf.equityCategory || "-"}</td>
                        <td>{mf.asset?.id ?? "-"}</td>
                        <td>{mf.status === true ? "Active" : mf.status === false ? "Inactive" : "-"}</td>
                        <td>{mf.lastUpdatedAt ? new Date(mf.lastUpdateAt).toLocaleString() : "-"}</td>
                        <td>
                          <button className="danger-button small-button"
                            onClick = {() => deleteMutualFund(mf.id)}>
                              Delete
                          </button>
                        </td>
                        
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <p className="empty-message">
                No mutual funds in the watchlist
              </p>
            )}
          </div>

          <div className="card delete-section">
            <h2>Delete Mutual Fund</h2>

            <Input
              label="Mutual Fund Watchlist ID"
              type="number"
              value={mutualFundDeleteId}
              onChange={setMutualFundDeleteId}
            />

            <button
              className="danger-button"
              onClick={deleteMutualFund}
            >
              Delete Mutual Fund
            </button>
          </div>
        </div>
      )}

      {activeTab === "commodities" && (
        <div className="two-column">
          <div className="card">
            <h2>Add Commodity</h2>

            <form onSubmit={addCommodity}>
              <Input
                label="Product ID"
                value={commodityForm.productId}
                onChange={(value) =>
                  setCommodityForm({
                    ...commodityForm,
                    productId: value
                  })
                }
              />

              <Input
                label="Symbol"
                value={commodityForm.symbol}
                onChange={(value) =>
                  setCommodityForm({
                    ...commodityForm,
                    symbol: value
                  })
                }
                required
              />

              <Input
                label="Name"
                value={commodityForm.name}
                onChange={(value) =>
                  setCommodityForm({
                    ...commodityForm,
                    name: value
                  })
                }
              />

              <Input
                label="Quotation"
                value={commodityForm.quotation}
                onChange={(value) =>
                  setCommodityForm({
                    ...commodityForm,
                    quotation: value
                  })
                }
              />

              <Input
                label="Unit"
                value={commodityForm.unit}
                onChange={(value) =>
                  setCommodityForm({
                    ...commodityForm,
                    unit: value
                  })
                }
              />

              <Select
                label="Exchange"
                value={commodityForm.exchange}
                onChange={(value) =>
                  setCommodityForm({
                    ...commodityForm,
                    exchange: value
                  })
                }
                options={[
                  "NSE",
                  "LSE",
                  "NasdaQ"
                ]}
              />

              <Input
                label="Asset ID"
                type="number"
                value={commodityForm.assetId}
                onChange={(value) =>
                  setCommodityForm({
                    ...commodityForm,
                    assetId: value
                  })
                }
                required
              />

              <Select
                label="Status"
                value={String(commodityForm.status)}
                onChange={(value) =>
                  setCommodityForm({
                    ...commodityForm,
                    status: value === "true"
                  })
                }
                options={[
                  "true",
                  "false"
                ]}
              />

              <button
                className="primary-button"
                type="submit"
              >
                Add Commodity
              </button>
            </form>
          </div>

          <div className="card">
            <h2>Commodities</h2>

            {commodities.length > 0 ? (
              <div className="table-wrapper">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Product ID</th>
                      <th>Symbol</th>
                      <th>Name</th>
                      <th>Quotation</th>
                      <th>Unit</th>
                      <th>Exchange</th>
                      <th>Asset ID</th>
                      <th>Status</th>
                      <th>Last Updated</th>
                      <th>Action</th>
                    </tr>
                  </thead>

                  <tbody>
                    {commodities.map( (commodity) =>(
                      <tr key = {commodity.id}>
                        <td>{commodity.id}</td>
                        <td>{commodity.productId}</td>
                        <td>{commodity.symbol}</td>
                        <td>{commodity.name}</td>
                        <td>{commodity.quotation}</td>
                        <td>{commodity.unit}</td>
                        <td>{commodity.exchange}</td>
                        <td>{commodity.asset?.id ?? "-"}</td>
                        <td>{commodity.status === true ? "Active" : commodity.status === false ? "Inactive" : "-"}</td>
                        <td>{commodity.lastUpdateAt ? new Date(commodity.lastUpdateAt).toLocaleString() : "-"}</td>
                        <td>
                          <button className="danger-button small-button"
                                  onClick = {() => deleteCommodity(commodity.id)}
                          >
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <p className="empty-message">
                No commodities in the watchlist
              </p>
            )}
          </div>

          <div className="card delete-section">
            <h2>Delete Commodity by ID</h2>

            <Input
              label="Commodity Watchlist ID"
              type="number"
              value={commodityDeleteId}
              onChange={setCommodityDeleteId}
            />

            <button
              className="danger-button"
              onClick={deleteCommodity}
            >
              Delete Commodity
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default Watchlists;
