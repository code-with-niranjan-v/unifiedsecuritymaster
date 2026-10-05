import { useEffect, useState } from "react";
import api from "../api";
import Input from "../components/Input";
import Select from "../components/Select";

const emptyForm = {
  isin: "",
  symbol: "",
  name: "",
  securityType: "",
  assertId: "",
  gicsSector: "",
  gicsGroup: "",
  gicsIndustry: "",
  gicsSubIndustry: "",
  issuerName: "",
  faceValue: "",
  exchangeCode: "",
  currencyCode: "",
  countryCode: "",
  status: "",
  listingDate: "",
  lotSize: "",
  equityCategory: ""
};

function SecurityMaster() {
  const [form, setForm] = useState(emptyForm);

  const [securities, setSecurities] = useState([]);

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const updateField = (field, value) => {
    setForm({
      ...form,
      [field]: value
    });
  };

  const extractList = (response) => {
    const data = response.data;

    if (Array.isArray(data)) {
      return data;
    }

    if (Array.isArray(data?.data)) {
      return data.data;
    }

    if (Array.isArray(data?.result)) {
      return data.result;
    }

    return [];
  };

  const loadSecurities = async () => {
    try {
      /*
       * Your backend's GET endpoint declares a request body.
       * Axios allows a GET request body through the `data` property.
       */
      const response = await api.get(
        "/security/get-all-security"
      );

      setSecurities(extractList(response));

      setError("");
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Unable to retrieve securities."
      );
    }
  };

  useEffect(() => {
    loadSecurities();
  }, []);

  const addSecurity = async (e) => {
    e.preventDefault();

    setMessage("");
    setError("");

    try {
      const payload = {
        isin: form.isin,
        symbol: form.symbol,
        name: form.name,
        securityType: form.securityType,
        equityCategory: form.equityCategory || null,
        assertId: form.assertId
          ? Number(form.assertId)
          : null,
        gicsSector: form.gicsSector,
        gicsGroup: form.gicsGroup,
        gicsIndustry: form.gicsIndustry,
        gicsSubIndustry: form.gicsSubIndustry,
        issuerName: form.issuerName,
        faceValue: form.faceValue,
        exchangeCode: form.exchangeCode,
        currencyCode: form.currencyCode,
        countryCode: form.countryCode,
        status: form.status,
        listingDate: form.listingDate || null,
        lotSize: form.lotSize
          ? Number(form.lotSize)
          : null
      };

      await api.post(
        "/security/add-security",
        payload
      );

      setMessage("Security added successfully.");
      setForm(emptyForm);

      await loadSecurities();
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Unable to add security."
      );
    }
  };

  const updateSecurity = async () => {
    if (!form.id) {
      setError(
        "Select a security from the table before updating."
      );
      return;
    }

    setMessage("");
    setError("");

    try {
      /*
       * The update endpoint accepts SecurityMaster,
       * not AddSecurityMasterDTO.
       *
       * Therefore the asset relationship is sent as:
       * asset: { id: ... }
       */
      const payload = {
        id: Number(form.id),
        isin: form.isin,
        symbol: form.symbol,
        name: form.name,
        securityType: form.securityType,
        equityCategory: form.equityCategory || null,

        asset: form.assertId
          ? {
              id: Number(form.assertId)
            }
          : null,

        gicsSector: form.gicsSector,
        gicsGroup: form.gicsGroup,
        gicsIndustry: form.gicsIndustry,
        gicsSubIndustry: form.gicsSubIndustry,
        issuerName: form.issuerName,
        faceValue: form.faceValue,
        exchangeCode: form.exchangeCode,
        currencyCode: form.currencyCode,
        countryCode: form.countryCode,
        status: form.status,
        listingDate: form.listingDate || null,
        lotSize: form.lotSize
          ? Number(form.lotSize)
          : null
      };

      await api.put(
        "/security/update-security",
        payload
      );

      setMessage("Security updated successfully.");

      setForm(emptyForm);

      await loadSecurities();
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Unable to update security."
      );
    }
  };

  const deleteSecurity = async (id) => {
    if (!window.confirm(`Delete security ${id}?`)) {
      return;
    }

    setMessage("");
    setError("");

    try {
      await api.delete(
        `/security/delete-security/${id}`
      );

      setMessage("Security deleted successfully.");

      await loadSecurities();
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Unable to delete security."
      );
    }
  };

  const selectSecurity = (security) => {
    setForm({
      id: security.id ?? "",
      isin: security.isin ?? "",
      symbol: security.symbol ?? "",
      name: security.name ?? "",
      securityType: security.securityType ?? "",

      /*
       * SecurityMaster has an Asset object.
       */
      assertId: security.asset?.id ?? "",

      gicsSector: security.gicsSector ?? "",
      gicsGroup: security.gicsGroup ?? "",
      gicsIndustry: security.gicsIndustry ?? "",
      gicsSubIndustry: security.gicsSubIndustry ?? "",
      issuerName: security.issuerName ?? "",
      faceValue: security.faceValue ?? "",
      exchangeCode: security.exchangeCode ?? "",
      currencyCode: security.currencyCode ?? "",
      countryCode: security.countryCode ?? "",
      status: security.status ?? "",
      listingDate: security.listingDate ?? "",
      lotSize: security.lotSize ?? "",
      equityCategory: security.equityCategory ?? ""
    });

    window.scrollTo({
      top: 0,
      behavior: "smooth"
    });
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Security Master</h1>
          <p>
            Add, update, view and delete security master records.
          </p>
        </div>

        <button
          className="secondary-button"
          onClick={loadSecurities}
        >
          Refresh
        </button>
      </div>

      {message && <div className="success-message">{message}</div>}
      {error && <div className="error-message">{error}</div>}

      <div className="card">
        <h2>
          {form.id
            ? `Update Security #${form.id}`
            : "Add Security"}
        </h2>

        <div className="form-grid">
          <Input
            label="ISIN"
            value={form.isin}
            onChange={(value) =>
              updateField("isin", value)
            }
          />

          <Input
            label="Symbol"
            value={form.symbol}
            onChange={(value) =>
              updateField("symbol", value)
            }
          />

          <Input
            label="Name"
            value={form.name}
            onChange={(value) =>
              updateField("name", value)
            }
            required
          />

          <Select
            label="Security Type"
            value={form.securityType}
            onChange={(value) =>
              updateField("securityType", value)
            }
            options={[
              "EQUITY",
              "MUTUAL_FUND",
              "ETF",
              "BOND",
              "COMMODITY"
            ]}
            required
          />

          {(["EQUITY", "MUTUAL_FUND"].includes(form.securityType)) && (
            <Select
              label="Equity Category"
              value={form.equityCategory}
              onChange={(value) => updateField("equityCategory", value)}
              options={["SMALL_CAP", "MID_CAP", "LARGE_CAP"]}
            />
          )}

          <Input
            label="Asset ID"
            type="number"
            value={form.assertId}
            onChange={(value) =>
              updateField("assertId", value)
            }
          />

          <Input
            label="GICS Sector"
            value={form.gicsSector}
            onChange={(value) =>
              updateField("gicsSector", value)
            }
          />

          <Input
            label="GICS Group"
            value={form.gicsGroup}
            onChange={(value) =>
              updateField("gicsGroup", value)
            }
          />

          <Input
            label="GICS Industry"
            value={form.gicsIndustry}
            onChange={(value) =>
              updateField("gicsIndustry", value)
            }
          />

          <Input
            label="GICS Sub Industry"
            value={form.gicsSubIndustry}
            onChange={(value) =>
              updateField("gicsSubIndustry", value)
            }
          />

          <Input
            label="Issuer Name"
            value={form.issuerName}
            onChange={(value) =>
              updateField("issuerName", value)
            }
          />

          <Input
            label="Face Value"
            value={form.faceValue}
            onChange={(value) =>
              updateField("faceValue", value)
            }
          />

          <Input
            label="Exchange Code"
            value={form.exchangeCode}
            onChange={(value) =>
              updateField("exchangeCode", value)
            }
            required
          />

          <Input
            label="Currency Code"
            value={form.currencyCode}
            onChange={(value) =>
              updateField("currencyCode", value)
            }
          />

          <Input
            label="Country Code"
            value={form.countryCode}
            onChange={(value) =>
              updateField("countryCode", value)
            }
          />

          <Input
            label="Status"
            value={form.status}
            onChange={(value) =>
              updateField("status", value)
            }
            required
          />

          <Input
            label="Listing Date"
            type="date"
            value={form.listingDate}
            onChange={(value) =>
              updateField("listingDate", value)
            }
          />

          <Input
            label="Lot Size"
            type="number"
            value={form.lotSize}
            onChange={(value) =>
              updateField("lotSize", value)
            }
          />
        </div>

        <div className="button-row">
          <button
            className="primary-button"
            onClick={addSecurity}
          >
            Add Security
          </button>

          {form.id && (
            <button
              className="secondary-button"
              onClick={updateSecurity}
            >
              Update Security
            </button>
          )}

          {form.id && (
            <button
              className="outline-button"
              onClick={() => setForm(emptyForm)}
            >
              Clear
            </button>
          )}
        </div>
      </div>

      <div className="card">
        <div className="section-header">
          <div>
            <h2>Security Records</h2>
            <p>
              Click a row to load it into the update form.
            </p>
          </div>
        </div>

        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>ISIN</th>
                <th>Symbol</th>
                <th>Name</th>
                <th>Type</th>
                <th>Equity Category</th>
                <th>Exchange</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>

            <tbody>
              {securities.map((security) => (
                <tr
                  key={security.id}
                  onClick={() =>
                    selectSecurity(security)
                  }
                  className="clickable-row"
                >
                  <td>{security.id}</td>
                  <td>{security.isin}</td>
                  <td>{security.symbol}</td>
                  <td>{security.name}</td>
                  <td>{security.securityType}</td>
                  <td>{security.equityCategory || "-"}</td>
                  <td>{security.exchangeCode}</td>
                  <td>{security.status}</td>

                  <td>
                    <button
                      className="danger-button small-button"
                      onClick={(e) => {
                        e.stopPropagation();
                        deleteSecurity(security.id);
                      }}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {securities.length === 0 && (
            <p className="empty-message">
              No security records found.
            </p>
          )}
        </div>
      </div>
    </div>
  );
}

export default SecurityMaster;
