var id
function wichcar(idin){
	id = idin;
	if(id === "f1"){
	document.getElementById(id).style.border= "6px solid #000";

	document.getElementById("f2").style.border= "none";
	document.getElementById("l1").style.border= "none";
	document.getElementById("l2").style.border= "none";
	}
	if(id === "f2"){
		document.getElementById(id).style.border= "6px solid #000";
	
		document.getElementById("f1").style.border= "none";
		document.getElementById("l1").style.border= "none";
		document.getElementById("l2").style.border= "none";
		}
		if(id === "l1"){
			document.getElementById(id).style.border= "6px solid #000";
		
			document.getElementById("f1").style.border= "none";
			document.getElementById("f2").style.border= "none";
			document.getElementById("l2").style.border= "none";
			}

			if(id === "l2"){
				document.getElementById(id).style.border= "6px solid #000";
				document.getElementById("l1").style.border= "none";
				document.getElementById("f1").style.border= "none";
				document.getElementById("f2").style.border= "none";
				
				}
}

//állapot csikok éhéség energia stb.
function thirst(hossz){
	document.getElementById("thirst").style.width = hossz;
}

function hunger(hossz){
	document.getElementById("hunger").style.width = hossz;
}


function energy(hossz){
	document.getElementById("energy").style.width = hossz;
}

function xp(hossz){
	document.getElementById("xp").style.width = hossz;
}

//Skillek nyitás
var skillStatus = 0;
function skills(){
	if(skillStatus===0){
		document.getElementById("skillList").style.display = "block";
		skillStatus = 1;
	}else{
		document.getElementById("skillList").style.display = "none";
		skillStatus = 0;
	}
}

function skill_close(){
	document.getElementById("skillList").style.display = "none";
}

//munkák nyitás
var jobStatus = 0;
function jobs(){
	if(jobStatus===0){
		document.getElementById("jobs").style.display = "block";
		jobStatus = 1;
  }else{
	document.getElementById("jobs").style.display = "none";
	jobStatus = 0;
  }
}

//munka zárás
function job_close(){
	document.getElementById("jobs").style.display = "none";
}


//inventory tabok
function skin_tab(){
	document.getElementById("skins").style.display ="block";
	document.getElementById("items").style.display ="none";
	document.getElementById("skin_tab").style.backgroundColor ="rgba(43, 42, 42, 0.4)";
	document.getElementById("item_tab").style.backgroundColor ="rgba(43, 42, 42, 0.8)";

}

function item_tab(){
	document.getElementById("items").style.display ="block";
	document.getElementById("skins").style.display ="none";
	document.getElementById("item_tab").style.backgroundColor ="rgba(43, 42, 42, 0.4)";
	document.getElementById("skin_tab").style.backgroundColor ="rgba(43, 42, 42, 0.8)";
} 
//inventory nyitás
var invStatus = 0;
function inv_sec(){
	if(invStatus===0){
		document.getElementById("inv_sec").style.display = "block";
		invStatus = 1;
  }else{
	document.getElementById("inv_sec").style.display = "none";
	invStatus = 0;
  }		  
	  }
// inventory zárás
function inv_close(){
	document.getElementById("inv_sec").style.display = "none";

}

//menuk
var menuStatus = 0;

	function openNav() {
		if(menuStatus===0){
		  document.getElementById("myNav").style.width = "80%";
		  menuStatus =1;
	}else{
		document.getElementById("myNav").style.width = "0%";
		menuStatus =0;
	}		  
		}

		function openNav_desk() {
			if(menuStatus===0){
			  document.getElementById("myNav_desk").style.width = "40%";
			  menuStatus =1;
		}else{
			document.getElementById("myNav_desk").style.width = "0%";
			 menuStatus =0;
		}		  
			}


function closeNav() {
  document.getElementById("myNav").style.width = "0%";
}
function closeNav_desk(close) {
	document.getElementById("myNav_desk").style.width = "0%";
  }

  // jelszó request validációk
function email_notEmpty(){
	var empty=true;
	var email = document.getElementById("email").value;
	
	if(email.length === 0){
		document.getElementById("email").className="input_error";
		document.getElementById("emptyemail").innerHTML = "Email cím megadása kötelező";
		empty=true;
	}else{
		empty=false;
	}

	if(empty === false){
	document.getElementById("pw_rq_submit").type="submit";
			document.getElementById("pw_rq_submit").id="submit_ready2";
		}
}
//regisztráció validáció
function input_notEmpty(){
	var empty=true;
	var fname = document.getElementById("first_name").value;
	var lname = document.getElementById("last_name").value;
	var uname = document.getElementById("username").value;
	var email = document.getElementById("email").value;
	var pw1 = document.getElementById("password1").value;
	var pw2 = document.getElementById("password2").value;


		if(fname.length === 0){
			document.getElementById("first_name").className="input_error";
			document.getElementById("empty").innerHTML = "A * jelölt mezők kötelezőek!";
			document.getElementById("felh_feltétel").checked = false;
			empty=true;
		}else{
			empty=false;
		}

		if(lname.length === 0){
			document.getElementById("last_name").className="input_error";
			document.getElementById("empty").innerHTML = "A * jelölt mezők kötelezőek!";
			document.getElementById("felh_feltétel").checked = false;
			empty=true;
		}else{
			empty=false;
		}

		if(uname.length === 0){
			document.getElementById("username").className="input_error";
			document.getElementById("empty").innerHTML = "A * jelölt mezők kötelezőek!";
			document.getElementById("felh_feltétel").checked = false;
			empty=true;
		}else{
			empty=false;
		}

		if(email.length === 0){
			document.getElementById("email").className="input_error";
			document.getElementById("empty").innerHTML = "A * jelölt mezők kötelezőek!";
			document.getElementById("felh_feltétel").checked = false;
			empty=true;
		}else{
			empty=false;
		}

		if(pw1.length === 0){
			document.getElementById("password1").className="input_error";
			document.getElementById("empty").innerHTML = "A * jelölt mezők kötelezőek!";
			document.getElementById("felh_feltétel").checked = false;
			empty=true;
		}else{
			empty=false;
		}

		if(pw2.length === 0){
			document.getElementById("password2").className="input_error";
			document.getElementById("empty").innerHTML = "A * jelölt mezők kötelezőek!";
			document.getElementById("felh_feltétel").checked = false;
			empty=true;
		}else{
			empty=false;
		}
		
		var felh_fel_check =document.getElementById("felh_feltétel");
		if(felh_fel_check.checked == false){
			empty=true;
			document.getElementById("felh_feltétel_lb").className="felh_feltétel_lb_error";
		}else{
			empty=false;
		}


		password_check(empty);

		
}
//jelszó validáció
function password_check(empty){ 
		//csak akkor fut ha minden mező ki van töltve

		var pw1 = document.getElementById("password1").value;
		var pw2 = document.getElementById("password2").value;
		if(!empty){
			if(pw1 !== pw2 || pw1.length === 0 || pw2.length === 0 ){

				document.getElementById("password1").className="input_error";
				document.getElementById("password2").className="input_error";
				document.getElementById("notMatchPassw").innerHTML = "Nem egyezik meg a két jelszó!";
				document.getElementById("felh_feltétel").checked = false;
			}else{
				document.getElementById("submit").type="submit";
				document.getElementById("submit").id="submit_ready";
			}
		}
}

function password_check2(){ 
	var empty2=true;
	//csak akkor fut ha minden mező ki van töltve

	var pw1 = document.getElementById("password1").value;
	var pw2 = document.getElementById("password2").value;
	if(!empty2){
		if(pw1 !== pw2 || pw1.length === 0 || pw2.length === 0 ){

			document.getElementById("password1").className="input_error";
			document.getElementById("password2").className="input_error";
			document.getElementById("notMatchPassw").innerHTML = "Nem egyezik meg a két jelszó!";
			
		}else{
			 empty2=false;
			document.getElementById("submit_jelszo_ch").type="submit";
			document.getElementById("submit_jelszo_ch").id="submit_ready";
		}
	}
}
//login validáció
function login_empty(){
	var empty_uname=true;
	var empty_pass=true;
	var unameLog = document.getElementById("userLogin").value;
	var pwLog = document.getElementById("passwordLogin").value;


	if(unameLog.length === 0){
		document.getElementById("emptyUname").innerHTML = "Adj meg egy felhasználónevet!";
		document.getElementById("userLogin").className="input_error";

		empty_uname=true;
	}else{
		empty_uname=false;
	}

	if(pwLog.length === 0){
		document.getElementById("emptyPass").innerHTML = "Adj meg egy jelszót!";
		document.getElementById("passwordLogin").className="input_error";

		empty_pass=true;
	}else{
		empty_pass=false;
	}

	if(empty_uname === false && empty_pass === false){
		document.getElementById("submit_login").type="submit";
		document.getElementById("submit_login").id="submit_ready";
	}

}

function caracterno(){
		document.getElementById("noi_tab").style.display ="block";
		document.getElementById("ferfi_tab").style.display ="none";
		document.getElementById("no").style.backgroundColor ="#ff0000";
		document.getElementById("ferfi").style.backgroundColor ="black";


}

function caracterferfi(){
	document.getElementById("noi_tab").style.display ="none";
	document.getElementById("ferfi_tab").style.display ="block";
	document.getElementById("ferfi").style.backgroundColor ="#ff0000";
	document.getElementById("no").style.backgroundColor ="black";
}
