import React, { useEffect, useState } from "react";
import AppHeader                from "../components/AppHeader";
import { useAuth }              from "../context/AuthContext";
import AuthorApplyModal         from "../pages/AuthorApplyModal";
import { Button }               from "../components/ui/button";

/* ───────────────────────────────────── */

export default function MyPage() {
    const { user }   = useAuth();
    const API_BASE   = process.env.REACT_APP_API_URL;

    /* -------- state -------- */
    const [detail,        setDetail]        = useState(null);
    const [authorStatus,  setAuthorStatus]  = useState(null);          // APPLIED | ACCEPTED | REJECTED | null
    const [pointBalance,  setPointBalance]  = useState(0);

    const [loading,       setLoading]       = useState(true);
    const [error,         setError]         = useState(null);

    const [subscribing,   setSubscribing]   = useState(false);         // 🔑 버튼 disabled 용
    const [showModal,     setShowModal]     = useState(false);
    const [applying,      setApplying]      = useState(false);

    /* -------- 구독 신청 -------- */
    const handleSubscribe = async () => {
        if (subscribing) return;
        setSubscribing(true);

        try {
            const res = await fetch(`${API_BASE}/users/request-subscription`, {
                method : "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization : `${user.tokenType ?? "Bearer"} ${user.token}`
                },
                body: JSON.stringify({ userId: user.userId })
            });

            if (!res.ok) throw new Error("구독 신청 실패 😢");
            alert("✅ 구독 신청이 완료되었습니다!");
            window.location.reload();                 // 새로고침으로 상태 반영
        } catch (e) {
            alert(e.message);
        } finally {
            setSubscribing(false);
        }
    };

    /* -------- 작가 신청 -------- */
    const handleSubmitAuthorApply = async (payload) => {
        setApplying(true);
        try {
            const res = await fetch(`${API_BASE}/authors/apply`, {
                method : "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization : `Bearer ${user.token}`
                },
                body: JSON.stringify({
                    authorEmail        : user.email,
                    authorName         : user.username,
                    ...payload
                })
            });
            if (!res.ok) throw new Error("작가 신청 실패");

            alert("✍️ 작가 신청이 접수되었습니다!");
            window.location.reload();
        } catch (e) {
            alert(e.message);
        } finally {
            setApplying(false);
        }
    };

    /* -------- 세 가지 정보 한 꺼번에 조회 -------- */
    useEffect(() => {
        if (!user?.token) return;

        const controller = new AbortController();

        (async () => {
            try {
                const [uRes, aRes, pRes] = await Promise.all([
                    fetch(`${API_BASE}/users/${user.userId}`, {
                        headers: {
                            "Content-Type": "application/json",
                            Authorization : `${user.tokenType ?? "Bearer"} ${user.token}`
                        },
                        signal: controller.signal
                    }),
                    fetch(`${API_BASE}/authors/my-data`, {
                        headers: { Authorization: `Bearer ${user.token}` },
                        signal : controller.signal
                    }),
                    fetch(`${API_BASE}/points/${user.userId}`, {
                        headers: { Authorization: `Bearer ${user.token}` },
                        signal : controller.signal
                    })
                ]);

                /* 1) 사용자 */
                if (!uRes.ok) throw new Error("사용자 정보를 불러올 수 없습니다.");
                setDetail(await uRes.json());

                /* 2) 작가 */
                if (aRes.ok) {
                    const { status } = await aRes.json();
                    setAuthorStatus(status);
                } else {
                    setAuthorStatus(null);
                }

                /* 3) 포인트 */
                if (pRes.ok) {
                    const { currentPoints } = await pRes.json();
                    setPointBalance(currentPoints);
                }
            } catch (e) {
                if (e.name !== "AbortError") setError(e.message);
            } finally {
                setLoading(false);
            }
        })();

        return () => controller.abort();
    }, [API_BASE, user]);

    /* --------  UI 스켈레톤 -------- */
    if (!user)          return <FullMsg>로그인이 필요합니다.</FullMsg>;
    if (loading)        return <FullMsg>로딩 중…</FullMsg>;
    if (error)          return <FullMsg>{error}</FullMsg>;
    if (!detail)        return <FullMsg>잠시만 기다려주세요.</FullMsg>;

    const { username, email, isKT, hasActiveSubscription } = detail;

    return (
        <div className="min-h-screen flex flex-col">
            <AppHeader />

            <main className="container mx-auto px-6 py-8">
                <h2 className="text-2xl font-bold mb-4">📌 마이페이지</h2>

                <section className="bg-white border rounded-md p-6 mb-6">
                    <div className="grid grid-cols-2 gap-x-8 gap-y-4">
                        {/* 닉네임 */}
                        <p><strong>🧑 닉네임:</strong> {username || "익명 사용자"}</p>

                        {/* 구독 상태 + 버튼 */}
                        <div className="flex items-center justify-between">
                            <p><strong>📦 구독 상태:</strong> {hasActiveSubscription ? "구독 중" : "구독 안 함"}</p>
                            {!hasActiveSubscription && (
                                <Button
                                    size="sm"
                                    disabled={subscribing}
                                    onClick={handleSubscribe}
                                >
                                    {subscribing ? "요청 중…" : "구독 신청"}
                                </Button>
                            )}
                        </div>

                        {/* 이메일 */}
                        <p><strong>📧 이메일:</strong> {email}</p>

                        {/* 작가 상태 + 버튼 */}
                        <div className="flex items-center justify-between">
                            <p><strong>✍️ 작가 여부:</strong> {authorStatus === "ACCEPTED" ? "작가입니다" : "아직 아닙니다"}</p>

                            {(authorStatus === null || authorStatus === "REJECTED") && (
                                <>
                                    <Button size="sm" onClick={() => setShowModal(true)}>작가 신청</Button>
                                    {showModal && (
                                        <AuthorApplyModal
                                            onClose={() => setShowModal(false)}
                                            onSubmit={handleSubmitAuthorApply}
                                            isSubmitting={applying}
                                        />
                                    )}
                                </>
                            )}
                        </div>

                        {/* KT · 포인트 */}
                        <p><strong>📱 KT 회원 여부:</strong> {isKT ? "예" : "아니오"}</p>
                        <p><strong>💰 포인트 잔액:</strong> {pointBalance.toLocaleString()} P</p>
                    </div>
                </section>
            </main>
        </div>
    );
}

/*  ⏸️ 화면 한 가운데 메시지용 컴포넌트 */
function FullMsg({ children }) {
    return (
        <div className="min-h-screen flex items-center justify-center">
            <p>{children}</p>
        </div>
    );
}
