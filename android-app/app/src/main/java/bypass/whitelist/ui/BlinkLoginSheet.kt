package bypass.whitelist.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import bypass.whitelist.R
import bypass.whitelist.util.Callback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class BlinkLoginSheet : BottomSheetDialogFragment() {

    private var loginLink: String = ""
    private var onCancel: Callback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.sheet_blink_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val copy = view.findViewById<MaterialButton>(R.id.buttonCopy)
        val open = view.findViewById<MaterialButton>(R.id.buttonOpen)
        val cancel = view.findViewById<MaterialButton>(R.id.buttonCancel)

        copy.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Blink login link", loginLink))
            Toast.makeText(requireContext(), R.string.blink_login_copied, Toast.LENGTH_SHORT).show()
        }
        open.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(loginLink)))
        }
        cancel.setOnClickListener {
            onCancel?.invoke()
            dismiss()
        }
        renderLink(view)
    }

    fun updateLink(link: String) {
        loginLink = link
        view?.let(::renderLink)
    }

    private fun renderLink(view: View) {
        val qrSize = (QR_SIZE_DP * resources.displayMetrics.density).toInt()
        val qr = BarcodeEncoder().encodeBitmap(loginLink, BarcodeFormat.QR_CODE, qrSize, qrSize)
        view.findViewById<ImageView>(R.id.loginQr).setImageBitmap(qr)
        view.findViewById<TextView>(R.id.loginLink).text = loginLink
    }

    companion object {
        const val TAG = "BlinkLoginSheet"

        private const val QR_SIZE_DP = 204

        fun show(manager: FragmentManager, loginLink: String, onCancel: Callback) {
            BlinkLoginSheet().apply {
                this.loginLink = loginLink
                this.onCancel = onCancel
            }.show(manager, TAG)
        }
    }
}
