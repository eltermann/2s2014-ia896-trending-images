// JavaScript Document


function buscar() {

	//busca por data	
	//if (document.getElementById("check_data").value == true){

		var datafrom = new Date(document.getElementById("datafrom").value);
		//document.getElementById("texto").innerHTML = data;
		this.from = datafrom.getTime()/1000;
		//alert(this.from);
		this.from += document.getElementById("amountHf").value*3600 + document.getElementById("amountMf").value*60;
		
		//para teste
		//document.getElementById("texto").innerHTML = this.from;
		
		var datato = new Date(document.getElementById("datato").value);
		this.to = datato.getTime()/1000;
		this.to += document.getElementById("amountHt").value*3600 + document.getElementById("amountMt").value*60;
		//para teste
		//document.getElementById("texto").innerHTML = this.to;
		
		$("#results_section").load(location.href + " #results_section");	
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
	/*$.ajax({
		type: "GET", 
		url: "http://54.149.70.51:54321/top10?from="+this.from+"&to="+this.to, 
		async:false, 
		dataType: "jsonp", 
		success: function(){alert("success");}, 		
		error: function(XMLHttpRequest, textStatus, errorThrown){
			alert("falha"); 
			for(i in XMLHttpRequest) {
				if(i!="channel")
					document.write(i +" : " + XMLHttpRequest[i] +"<br>");
			}
		}
	});*/
	
	//document.write("http://54.149.70.51:54321/top10?from="+this.from+"&to="+this.to);
	
	//$.getJSON("http://54.149.70.51:54321/top10", '{"from" :' + this.from + ', "to" : ' + this.to + '}', exibe(data));
	
	$.ajax({
		type: "GET", 
		url: "http://54.149.70.51:54321/top10?from="+this.from+"&to="+this.to,
		dataType: 'jsonp', 	
	});
	
	/*
	$.get("http://nexus.prefeitura.unicamp.br/Informatica/json.php?bug_id=6336", 
		function( data ) {
			$( ".result" ).html( data );
			alert( "Load was performed." );
		}
	);*/
	/*
	$.get("http://nexus.prefeitura.unicamp.br/Informatica/json.php?bug_id=6336")
		.done(function( data ) {
			alert( "Data Loaded: " + data );
		});*/
	

	
	//exibe();
	//document.getElementById("texto").innerHTML = data;

}

function jsonCallback(data){
	//obj = JSON.parse(data);
	obj = data;

	var image
	var count

	$(document.getElementById("results_section")).show();
	//mostra as imagens, caso retorne alguma ou retorna msg de erro, caso o servidor de erro
	if (obj.status == "ok"){
		//mostra resultado
		document.getElementById("results_title").innerHTML = "Resultados:";
		for(i=0;i<10;i++){
			//count = "count_image_" + i
			//header = "header_img"+i
			count = "A imagem acima apareceu " + obj.data[i].absolute + " vezes. Valor normalizado: " + obj.data[i].normalized;
			header = (i+1)+"ª posição";
			//document.getElementById(count).innerHTML = "A seguinte imagem apareceu " + obj.data[i].absolute + " vezes. Valor normalizado: " + obj.data[i].normalized;
			//document.getElementById(header).innerHTML = (i+1)+"ª posição";

			
			/**********Transicao - GALLERIA
			ulimage = "ulimage_"+i;
			image = "#image_"+i;
			$(image).empty();
			//document.getElementById(ulimage).innerHTML = "";
			
			//apaga as imagens antigas
			//$("#"+ulimage).empty();
			document.getElementById("image_"+i).insertAdjacentHTML("afterBegin", "<ul id='"+ulimage+"'> </ul>");
			
			for(j=0;j<10;j++){				

					if(j<obj.data[i].imgs.length){
						
						document.getElementById(ulimage).insertAdjacentHTML("afterBegin", "<li> <img src='"+obj.data[i].imgs[j]+"' alt='"+count+"' title='"+header+"'/></li>");
						//document.write(obj.data[i].imgs[j]);
					} 	
					 
         	}
			$( document ).ready(function() {
    			$(image).puigalleria({ panelHeight: 400}).startSlideshow;
  			});
			  
			
			GALLERIA termina aqui********/
			
			/******Transicao - LIGHTBOX*/
			image = "#image_"+i;
						
			//apaga as imagens antigas
			
			
			document.getElementById("count_image_" + i).innerHTML = count;
			document.getElementById("header_img"+i).innerHTML = header;

			
			
			//if(document.getElementById("image_"+i).firstElementChild != null){
				$(image).empty();
			//}
			console.log(document.getElementById("image_"+i));
			
			for(j=0;j<5;j++){				

					if(j<obj.data[i].imgs.length){
						
						
							document.getElementById("image_"+i).insertAdjacentHTML("afterBegin", "<a href='"+
							obj.data[i].imgs[j]+"'> <img src='"+obj.data[i].imgs[j]+"' alt='"+count+"' title='"+header+"'  height='120'/></a>");
					
					} 	
					 
         	}
			
			$(image).puilightbox(); 

			/*LIGHTBOX termina aqui*******/			
			
		}
		document.getElementById("texto").innerHTML = "";
		

	}
	else {
		//mensagem de erro
		document.getElementById("texto").innerHTML = "Não conseguimos processar sua busca. Por favor, verifique as datas escolhidas e realize nova busca.";
		$(document.getElementById("results_section")).hide();
	}
	
	//$(document.getElementById("results_section")).show();

}


function datepicker() {
    $( "#datepicker" ).datepicker();
}


function esconde_resultados(){
	$(document.getElementById("results_section")).hide();
	document.getElementById("texto").innerHTML = "";
}
