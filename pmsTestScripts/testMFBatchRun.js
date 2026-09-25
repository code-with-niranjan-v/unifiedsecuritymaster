import axios from 'axios'
const runBatch = async ()=>{
    const res = await axios.post("http://localhost:8081/api/v1/batch/mutual-fund/run","");
    console.log(res);
}

await runBatch()