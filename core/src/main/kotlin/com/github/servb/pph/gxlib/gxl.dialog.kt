package com.github.servb.pph.gxlib

import com.github.servb.pph.gxlib.gxlmetrics.AlignRect
import com.github.servb.pph.gxlib.gxlmetrics.Alignment
import com.github.servb.pph.gxlib.gxlmetrics.Rect
import com.github.servb.pph.gxlib.gxlmetrics.Size
import com.github.servb.pph.gxlib.gxlview.VIEWCLSID
import com.github.servb.pph.gxlib.gxlview.iView
import com.github.servb.pph.gxlib.gxlviewmgr.iViewMgr

enum class DLG_RETCODE {
    DRC_UNDEFINED,
    DRC_OK,
    DRC_CANCEL,
    DRC_YES,
    DRC_NO;
}

abstract class iDialog(pViewMgr: iViewMgr)
    : iView(pViewMgr, Rect(), VIEWCLSID.MODAL_DIALOG, 0, ViewState.Enabled.v) {
    private var m_retCode = DLG_RETCODE.DRC_UNDEFINED

    fun DoModal(): DLG_RETCODE {
        Center()
        OnCreateDlg()
        SetVisible()
        m_pMgr.PushModalDlg(this)
        while (m_pMgr.App().Cycle() && m_retCode == DLG_RETCODE.DRC_UNDEFINED) {}
        val pDlg = m_pMgr.PopModalDlg()
        check(pDlg == this)

        return m_retCode
    }

    // Pure virtuals
    abstract fun GetDlgMetrics(): Size
    abstract fun OnCreateDlg()
    open fun OnPlace(rect: Rect) {}

    // Virtuals
    open fun KeyDown(key: Int) = false
    open fun KeyUp(key: Int) = false

    protected fun IsValidDialog() = m_retCode == DLG_RETCODE.DRC_UNDEFINED

    protected fun Center() {
        val rect = Rect(AlignRect(GetDlgMetrics(), Rect(m_pMgr.Metrics()), Alignment.AlignCenter))
        OnPlace(rect)
        SetRect(rect)
    }

    protected fun EndDialog(retCode: DLG_RETCODE): Boolean {
        if (m_retCode != DLG_RETCODE.DRC_UNDEFINED) {
            return false
        }
        m_retCode = retCode
        return true
    }

    override fun Invalidate() {
        m_bNeedRedraw = true
        m_pMgr.CurView()?.Invalidate()
    }
}
