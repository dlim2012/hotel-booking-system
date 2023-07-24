import {bedsMap} from "../assets/Lists";

const bedNameMap = {
    "KING": "king",
    "QUEEN": "queen",
    "SOFA_BED": "sofa bed",
    "FULL": "full",
    "TWIN": "twin",
    "SINGLE": "single"
}

const getBedText = (bedInfoList) => {
    console.log(bedInfoList)
    var bedInfo = ""
    if (bedInfoList.length === 1){
        var quantity = bedInfoList[0].quantity;
        var size = bedNameMap[bedInfoList[0].size]
        if (bedInfoList[0].size !== "SOFA_BED"){
            size += " bed";
        }
        if (quantity > 1){
            size += "s";
        }
        bedInfo =  quantity + size;
    } else {
        var numBed = 0;
        for (var i=0; i<bedInfoList.length; i++) {
            numBed += bedInfoList[i].quantity;
        }


        bedInfo = numBed + " beds ("
        for (var i=0; i<bedInfoList.length; i++){
            var quantity = bedInfoList[i].quantity;
            var size = bedsMap[bedInfoList[i].size]
            bedInfo += quantity + " " + size + " bed" + (quantity > 1 ? "s": "")
            if (i < bedInfoList.length - 1){
                bedInfo += ", "
            }
        }
        bedInfo += ")"
    }
    return bedInfo
}


export default getBedText;