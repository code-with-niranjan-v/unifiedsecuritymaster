import axios from 'axios';

const URL = 'https://www.niftyindices.com/BackPage/getTotalReturnIndexString';

const getIndex = async () => {

  // MUST be a hand-built string with SINGLE quotes - not an object, not JSON.stringify
  const cinfo = `{'name':'NIFTY 100','startDate':'25-Sep-2025','endDate':'25-Sep-2026','indexName':'NIFTY 100'}`;

  const res = await axios.post(
    URL,
    { cinfo },
    {
      headers: {
        'Content-Type':     'application/json; charset=UTF-8',
        'X-Requested-With': 'XMLHttpRequest',
        'Referer':          'https://www.niftyindices.com/reports/historical-data',
        'Origin':           'https://www.niftyindices.com',
        'Accept':           'application/json, text/javascript, */*; q=0.01',
        'Accept-Language':  'en-US,en;q=0.9',
        'User-Agent':       'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 ' +
                            '(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36'
      },
      timeout: 60000
    }
  );

  // response is DOUBLE encoded: res.data.d is a JSON string
  console.log(res.data);

//   console.log('rows:', rows.length);
//   console.log('first:', rows[0]);
//   return rows;
};

await getIndex();