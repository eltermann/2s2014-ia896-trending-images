// JavaScript Document


function buscar() {

	//busca por data	
	//if (document.getElementById("check_data").value == true){

		var datafrom = new Date(document.getElementById("datafrom").value);
		//document.getElementById("texto").innerHTML = data;
		this.from = datafrom.getTime()/1000;
		//para teste
		//document.getElementById("texto").innerHTML = this.from;
		
		var datato = new Date(document.getElementById("datato").value);
		this.to = datato.getTime()/1000;
		//para teste
		//document.getElementById("texto").innerHTML = this.to;
		
			
		//tratar as datas nulas de from e to 	
		
	//}
	
	//colocar datas no formato json
	//var json_msg = '{"from" :' + this.from + ', "to" : ' + this.to + '}';
	//url = "http://54.149.70.51:54321/top10?from="+this.from+"&to="+this.to
	
	
	//faz a requisicao para o servidor
	//$.get("http://54.149.70.51:54321/top10?from=1417824000&to=1417996740",function(data,status){
    //document.getElementById("texto").innerHTML = data.status;
     //});
  
	//$.get("http://54.149.70.51:54321/top10", { from:1417824000, to:1417996740 }, function(data){exibe(data);});
	

	//$.getJSON("http://54.149.70.51:54321/top10", json_msg, function(data){exibe(data);});
	 //document.getElementById("texto").innerHTML = "Fernando legal";
	 //exibe(data));
	 this.from = 1418169600;
	 this.to = 1418173200;
	$.ajax({type: "GET", url: "http://54.149.70.51:54321/top10?from="+this.from+"&to="+this.to, async:false, dataType: "jsonp", success: function(){alert("success");}, error: function(){alert("falha");}});
	
	alert = "http://54.149.70.51:54321/top10?from="+this.from+"&to="+this.to;
	//exibe(data));
	//document.getElementById("texto").innerHTML = data;
	//exibe(data[0]);
}

function exibe(data){
	var data = '{"status": "ok", "data": [{"imgs": ["http://pbs.twimg.com/media/B39Oy1_CIAA5pux.jpg", "http://pbs.twimg.com/media/B39O_mJCQAAp-L9.jpg"], "absolute": 296, "id": "547f8687c4e22521aba0afa5", "normalized": "1.000"},{"imgs": ["http://pbs.twimg.com/media/B39NYNyCEAApxu8.jpg", "http://pbs.twimg.com/media/B39Nqv3CAAATQ3r.jpg"], "absolute": 235, "id": "547f8690c4e22521aba0afaa", "normalized": "0.794"},{"imgs": ["http://pbs.twimg.com/media/B0XvfPMCUAAVNyv.jpg"], "absolute": 171, "id": "547f8807c4e22521aba0b0b7", "normalized": "0.578"},{"imgs": ["http://pbs.twimg.com/media/B39oUINCMAEqcfH.jpg", "http://pbs.twimg.com/media/B39oUHgCAAAoVgx.jpg", "http://pbs.twimg.com/media/B39oUICCYAApRXk.jpg"], "absolute": 159, "id": "547f89aec4e22521aba0b20d", "normalized": "0.537"},{"imgs": ["http://pbs.twimg.com/media/B39Ll4LCYAAPwwR.jpg"], "absolute": 149, "id": "547f8692c4e22521aba0afad", "normalized": "0.503"},{"imgs": ["http://pbs.twimg.com/media/B39KY4rCUAEk8J0.jpg"], "absolute": 148, "id": "547f8685c4e22521aba0afa3", "normalized": "0.500"},{"imgs": ["http://pbs.twimg.com/media/B2wL0KFCMAAcb1Q.png"], "absolute": 145, "id": "547f870dc4e22521aba0b00b", "normalized": "0.490"},{"imgs": ["http://pbs.twimg.com/media/B39MQMCCYAEii14.jpg"], "absolute": 145, "id": "547f8688c4e22521aba0afa6", "normalized": "0.490"},{"imgs": ["http://pbs.twimg.com/media/B39MbFKCcAAlszc.png"], "absolute": 138, "id": "547f86c1c4e22521aba0afcf", "normalized": "0.466"},{"imgs": ["http://pbs.twimg.com/media/B39NNLRCQAAgrnv.png"], "absolute": 136, "id": "547f8704c4e22521aba0b004", "normalized": "0.459"}], "query_time_us": 140671}';
	obj = JSON.parse(data);
	document.getElementById("texto").innerHTML = data;
	//teste pra ver se entra nesta funcao qdo faco o get pro servidor
	//document.getElementById("texto").innerHTML = data;
	
	var image
	var count
	$(document.getElementById("results_section")).show();
	//mostra as imagens, caso retorne alguma ou retorna msg de erro, caso o servidor de erro
	if (obj.status == "ok"){
		//mostra resultado
		document.getElementById("results_title").innerHTML = "Resultados:";
		for(i=0;i<10;i++){
			count = "count_image_" + i
			document.getElementById(count).innerHTML = "A seguinte imagem apareceu " + obj.data[i].absolute + " vezes. Valor normalizado: " + obj.data[i].normalized;
			for(j=0;j<obj.data[i].imgs.length;j++){				
					image = "img" + i + "-" + j;
					document.getElementById(image).src = obj.data[i].imgs[j];
			}
		}
	}
	else {
		//mensagem de erro
		document.getElementById("texto").innerHTML = "Não conseguimos processar sua busca. Por favor, verifique as datas escolhidas e realize nova busca.";
	}
	
	//$(document.getElementById("results_section")).show();

}


function datepicker() {
    ( "#datepicker" ).datepicker();
}


function esconde_resultados(){
	$(document.getElementById("results_section")).hide();
}
