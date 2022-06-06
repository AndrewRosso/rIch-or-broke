const general_url = './app/';


function loadResultGif() {
    let currencyCode = $("#codes_select").val();
    $.ajax({
        url: general_url + 'gif' + '?currencyCode=' + currencyCode,
        method: 'GET',
        dataType: "json",
        complete: function (data) {
            let content = JSON.parse(data.responseText);
            let img = document.createElement("img");
            let gifName = document.createElement("p");
            gifName.textContent = content.title;
            let gifKey = document.createElement("p");
            gifKey.textContent = content.gifTag;
            img.src = content.url;
            let out = document.querySelector("#out");
            out.innerHTML = '';
            out.insertAdjacentElement("afterbegin", img);
            out.insertAdjacentElement("afterbegin", gifName);
            out.insertAdjacentElement("afterbegin", gifKey);
        }
    })
}

function loadCodeForSelect() {
    $.ajax({
        url: general_url + 'currency-codes',
        method: 'GET',
        complete: function (data) {
            let codesList = JSON.parse(data.responseText);
            let select = document.querySelector("#codes_select");
            select.innerHTML = '';
            for (let i = 0; i < codesList.length; i++) {
                let option = document.createElement("option");
                option.value = codesList[i];
                option.text = codesList[i];
                select.insertAdjacentElement("beforeend", option);
            }
        }
    })
}
