package com.citizenconnect.app.ui.home
import android.content.Intent; import android.os.Bundle; import android.os.Handler; import android.os.Looper
import android.webkit.JavascriptInterface; import android.webkit.WebView; import android.webkit.WebViewClient; import android.widget.Toast
import androidx.activity.viewModels; import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.citizenconnect.app.R; import com.citizenconnect.app.databinding.ActivityHomeBinding
import com.citizenconnect.app.ui.adapters.ComplaintAdapter
import com.citizenconnect.app.ui.auth.LoginActivity; import com.citizenconnect.app.ui.complaints.MyComplaintsActivity
import com.citizenconnect.app.ui.detail.ComplaintDetailActivity; import com.citizenconnect.app.ui.profile.ProfileActivity
import com.citizenconnect.app.ui.report.ReportIssueActivity; import com.citizenconnect.app.ui.chatbot.ChatBotActivity
import com.citizenconnect.app.utils.*; import com.citizenconnect.app.viewmodel.ComplaintViewModel
import kotlinx.coroutines.Dispatchers; import kotlinx.coroutines.Job; import kotlinx.coroutines.delay; import kotlinx.coroutines.launch; import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {
    private lateinit var b: ActivityHomeBinding
    private val vm: ComplaintViewModel by viewModels()
    private lateinit var adapter: ComplaintAdapter
    private var mapLoaded = false
    private var updateJob: Job? = null
    private val loc by lazy { LocationHelper(this) }

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        SessionManager.applyLanguage(this)
        b = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(b.root)
        
        val uid = SessionManager.uid(this) ?: run {
            startActivity(Intent(this, LoginActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }); return
        }
        setupRecyclerView(); setupBottomNav(); setupClicks()
        
        b.tvGreeting.text = getGreeting(this)
        b.tvUserName.text = SessionManager.name(this).split(" ").firstOrNull() ?: "Citizen"
        
        Handler(Looper.getMainLooper()).postDelayed({ setupMap() }, 800)
        
        // Load GLOBAL complaints for Home Screen
        vm.load(uid, myComplaintsOnly = false)
        observe()
    }

    private fun setupMap() {
        try {
            b.mapWebView.apply {
                settings.javaScriptEnabled = true; settings.domStorageEnabled = true
                addJavascriptInterface(WebAppInterface(), "Android")
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        mapLoaded = true
                        vm.complaints.value?.let { updateMapMarkers(it) }
                    }
                }
                loadDataWithBaseURL(null, osmHtml(20.59, 78.96, 5), "text/html", "UTF-8", null)
            }
        } catch (e: Exception) { b.mapWebView.gone() }
    }

    private inner class WebAppInterface {
        @JavascriptInterface
        fun onUpvoteClick(id: String) {
            runOnUiThread {
                vm.upvote(id)
                Toast.makeText(this@HomeActivity, "Supporting this issue!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun osmHtml(lat: Double, lng: Double, zoom: Int) = """
        <!DOCTYPE html><html><head>
        <meta name="viewport" content="width=device-width,initial-scale=1"/>
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
        <script src="https://cdn.jsdelivr.net/gh/Leaflet/Leaflet.heat/dist/leaflet-heat.js"></script>
        <style>
            *{margin:0;padding:0}#map{width:100vw;height:100vh;background:#f0f0f0}
            .popup-btn{background:#1565C0;color:white;border:none;padding:6dp 10dp;border-radius:4dp;font-size:11px;margin-top:8px;cursor:pointer;width:100%}
        </style>
        </head><body><div id="map"></div>
        <script>
        var map=L.map("map",{zoomControl:false}).setView([$lat,$lng],$zoom);
        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",{maxZoom:19}).addTo(map);
        var markerLayer = L.layerGroup().addTo(map);
        var heatLayer = L.heatLayer([], {radius: 25, blur: 15, maxZoom: 17}).addTo(map);
        
        function updateMarkers(markersJson) {
            markerLayer.clearLayers();
            var heatPoints = [];
            markersJson.forEach(function(m) {
                var popupContent = '<b>' + m.title + '</b><br>' + 
                                 'Votes: ' + m.votes + '<br>' +
                                 '<button class="popup-btn" onclick="Android.onUpvoteClick(\'' + m.id + '\')">ME TOO</button>';
                
                L.circleMarker([m.lat, m.lng], {color: m.col, radius: 6, fillOpacity: 0.8, weight: 1})
                 .addTo(markerLayer).bindPopup(popupContent);
                
                var weight = 0.2 + (Math.min(m.votes, 20) / 20);
                heatPoints.push([m.lat, m.lng, weight]);
            });
            heatLayer.setLatLngs(heatPoints);
        }
        
        function centerMap(la, ln) {
            map.setView([la, ln], 15);
            L.circleMarker([la, ln], {color: '#F44336', radius: 10, weight: 3}).addTo(map).bindPopup("You are here").openPopup();
        }
        
        function zoomIn() { map.setZoom(map.getZoom() + 1); }
        function zoomOut() { map.setZoom(map.getZoom() - 1); }
        </script></body></html>""".trimIndent()

    private fun setupRecyclerView() {
        adapter = ComplaintAdapter(emptyList()) { c ->
            startActivity(Intent(this, ComplaintDetailActivity::class.java).putExtra("complaint_id", c.id))
        }
        b.rvComplaints.adapter = adapter
        b.rvComplaints.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        b.rvComplaints.isNestedScrollingEnabled = false
    }

    private fun setupBottomNav() {
        b.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_report -> { go(ReportIssueActivity::class.java); false }
                R.id.nav_complaints -> { go(MyComplaintsActivity::class.java); false }
                R.id.nav_profile -> { go(ProfileActivity::class.java); false }
                else -> false
            }
        }
    }

    private fun setupClicks() {
        b.fabReport.setOnClickListener { go(ReportIssueActivity::class.java) }
        b.cardReportIssue.setOnClickListener { go(ReportIssueActivity::class.java) }
        b.cardTrack.setOnClickListener { go(MyComplaintsActivity::class.java) }
        b.tvViewAll.setOnClickListener { go(MyComplaintsActivity::class.java) }
        b.ivProfile.setOnClickListener { go(ProfileActivity::class.java) }
        b.fabChat.setOnClickListener { go(ChatBotActivity::class.java) }
        
        b.btnMyCity.setOnClickListener { 
            loc.getCurrentLocation(
                onResult = { la, ln, _ -> 
                    b.mapWebView.evaluateJavascript("centerMap($la, $ln)", null)
                },
                onError = { Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show() }
            )
        }
        
        // Zoom Clicks
        b.btnZoomIn.setOnClickListener { b.mapWebView.evaluateJavascript("zoomIn()", null) }
        b.btnZoomOut.setOnClickListener { b.mapWebView.evaluateJavascript("zoomOut()", null) }
    }

    private fun <T> go(cls: Class<T>) = startActivity(Intent(this, cls))

    private fun observe() {
        vm.complaints.observe(this) { list ->
            // Use currentUserId to calculate counts correctly
            b.tvTotalCount.text    = vm.totalCount().toString()
            b.tvResolvedCount.text = vm.resolvedCount().toString()
            b.tvPendingCount.text  = vm.pendingCount().toString()
            
            // For the bottom list, only show current user's issues
            val uid = SessionManager.uid(this)
            adapter.updateData(list.filter { it.userId == uid }.take(5))
            if (list.none { it.userId == uid }) b.tvEmpty.visible() else b.tvEmpty.gone()
            
            // Map still shows EVERYTHING (global)
            updateMapMarkers(list)
        }
    }

    private fun updateMapMarkers(list: List<com.citizenconnect.app.model.Complaint>) {
        if (!mapLoaded) return
        updateJob?.cancel()
        updateJob = lifecycleScope.launch(Dispatchers.Default) {
            delay(500)
            val markersJson = list.filter { it.latitude != 0.0 }.take(100).map { c ->
                val col = when(c.status){"RESOLVED"->"#2E7D32";"IN_PROGRESS"->"#7B1FA2";"ACKNOWLEDGED"->"#1565C0";else->"#FF8F00"}
                "{id:'${c.id}', lat:${c.latitude}, lng:${c.longitude}, col:'$col', votes:${c.upvoteCount}, title:'${c.title.replace("'","\\'").replace("\n"," ")}'}"
            }.joinToString(",", "[", "]")
            
            withContext(Dispatchers.Main) {
                b.mapWebView.evaluateJavascript("try{ updateMarkers($markersJson); }catch(e){}", null)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        SessionManager.applyLanguage(this)
        SessionManager.uid(this)?.let { vm.load(it, myComplaintsOnly = false) }
        b.tvGreeting.text = getGreeting(this)
        b.tvUserName.text = SessionManager.name(this).split(" ").firstOrNull() ?: "Citizen"
    }
}
