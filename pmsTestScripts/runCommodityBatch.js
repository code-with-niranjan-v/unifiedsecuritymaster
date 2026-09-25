import axios from 'axios'
const runBatch = async ()=>{
    const res = await axios.post("http://localhost:8081/api/v1/batch/commodity/run","");
    console.log(res);
}

runBatch()