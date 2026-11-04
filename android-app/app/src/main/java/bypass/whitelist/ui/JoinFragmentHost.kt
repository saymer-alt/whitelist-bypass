package bypass.whitelist.ui

import bypass.whitelist.tunnel.VpnStatus

interface JoinFragmentHost {
    fun appendLog(message: String)
    fun onJoinStatus(status: VpnStatus)
    fun onJoinStatusText(text: String)
    fun onLoginLink(link: String)
    fun requestVpn()
    fun setJoinUiVisible(visible: Boolean)
    fun onJoinCancel()
}
