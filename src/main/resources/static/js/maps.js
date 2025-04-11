// Функция инициализации карты
async function initMap() {
    // Координаты вашего магазина
    const position = { lat: 55.7558, lng: 37.6173 }; // Пример: центр Москвы

    // Используем динамический импорт
    const { Map } = await google.maps.importLibrary("maps");
    const { AdvancedMarkerElement } = await google.maps.importLibrary("marker");

    // Создаем карту
    const map = new Map(document.getElementById("map"), {
        center: position,
        zoom: 15,
        mapId: "FLOWER_SHOP_MAP",
        styles: [
            // Здесь можно настроить стили карты в нейтральных тонах
        ]
    });

    // Добавляем продвинутый маркер
    const marker = new AdvancedMarkerElement({
        position: position,
        map: map,
        title: "BLOOM - Цветочный магазин"
    });
}

// Используем новый рекомендованный метод загрузки API
(g=>{var h,a,k,p="The Google Maps JavaScript API",c="google",l="importLibrary",q="__ib__",m=document,b=window;b=b[c]||(b[c]={});var d=b.maps||(b.maps={}),r=new Set,e=new URLSearchParams,u=()=>h||(h=new Promise(async(f,n)=>{await (a=m.createElement("script"));e.set("libraries",[...r]+"");for(k in g)e.set(k.replace(/[A-Z]/g,t=>"_"+t[0].toLowerCase()),g[k]);e.set("callback",c+".maps."+q);a.src=`https://maps.${c}apis.com/maps/api/js?`+e;d[q]=f;a.onerror=()=>h=n(Error(p+" could not load."));a.nonce=m.querySelector("script[nonce]")?.nonce||"";m.head.append(a)}));d[l]?console.warn(p+" only loads once. Ignoring:",g):d[l]=(f,...n)=>r.add(f)&&u().then(()=>d[l](f,...n))})({
    key: "AIzaSyD_VNxA28gpMpeNrHVA_qa849HACeLyGsw",
    v: "weekly"
});

window.initMap = initMap;