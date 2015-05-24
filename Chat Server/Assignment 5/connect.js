function retrieveName(){
	name = window.prompt("Please enter a username:","");
	return name;
}

function addNewUser(userName){
	var newUser = document.createElement("p");
	newUser.innerHTML = userName;
	newUser.setAttribute("class","user");
	document.getElementById("usersDiv").appendChild(newUser);
}


function request_users_list(userlist){
			var users = userlist.split(",");
			remove_users();  
			for(var i = 1; i < users.length;i++){
						addNewUser(users[i]);
			}

			$(".user").click(function(){
				if(selected != null && (selected.text() == $(this).text())){
					$(this).css("background","#E8E8E8");
					selected = null;
				}   
				else{
				$(".user").css("background","#E8E8E8");
				selected = $(this);
  				$(this).css("background","#47A3FF");
  				}
  			});
}

function remove_users(){
	$(".user").remove();
}

