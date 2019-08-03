/**
 * 
 */
function letterToNum(msg){
	var pattern = /[a-zA-Z]/g;
	msg = msg.replace(pattern,"");
	if(msg==""){
		msg=1;
	}
	return msg;
}