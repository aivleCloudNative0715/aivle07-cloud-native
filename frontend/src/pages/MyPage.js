// src/pages/MyPage.jsx
import React, { useEffect, useState } from "react";
import AppHeader from "../components/AppHeader";
import { useAuth } from "../context/AuthContext";
import AuthorApplyModal from "../pages/AuthorApplyModal";

export default function MyPage() {
    const { user } = useAuth();
    const [detail, setDetail] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const API_BASE = process.env.REACT_APP_API_URL;
    const [subscribing, setSubscribing] = useState(false);
    const [showAuthorModal, setShowAuthorModal] = useState(false);
    const [applyingAuthor, setApplyingAuthor] = useState(false);
    const [authorStatus, setAuthorStatus] = useState(null); // 작가 신청 상태

    const handleSubscribe = async () => {
        if (subscribing) return;
        setSubscribing(true);

        try {
            const res = await fetch(`${API_BASE}/users/request-subscription`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `${user.tokenType ?? "Bearer"} ${user.token}`,
                },
                body: JSON.stringify({ userId: user.userId }), // ✅ 필요 시 수정
            });

            if (!res.ok) throw new Error("구독 신청 실패");

            const updated = await res.json();
            setDetail((prev) => ({ ...prev, subscribed: updated.hasActiveSubscription }));

            alert("구독 신청이 완료되었습니다!");

            window.location.reload();
        } catch (e) {
            alert(e.message || "구독 신청 중 오류가 발생했습니다.");
        } finally {
            setSubscribing(false);
        }
    };

    const handleSubmitAuthorApply = async ({ bio, portfolio, representativeWork }) => {
        setApplyingAuthor(true);
        try {
            const res = await fetch(`${API_BASE}/authors/apply`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${user.token}`,
                },
                body: JSON.stringify({
                    authorEmail: user.email,
                    authorName: user.username,
                    bio,
                    representativeWork,
                    portfolio,
                }),
            });

            if (!res.ok) throw new Error("작가 신청 실패");

            alert("✅ 작가 신청이 완료되었습니다!");
            window.location.reload();
        } catch (e) {
            alert(e.message || "❌ 신청 중 오류 발생");
        } finally {
            setApplyingAuthor(false);
        }
    };


    useEffect(() => {
        if (!user || !user.token) return;

        const controller = new AbortController();

        (async () => {
            try {
                const [userRes, authorRes] = await Promise.all([
                    fetch(`${API_BASE}/users/${user.userId}`, {
                        method: "GET",
                        headers: {
                            "Content-Type": "application/json",
                            Authorization: `${user.tokenType ?? "Bearer"} ${user.token}`,
                        },
                        signal: controller.signal,
                    }),
                    fetch(`${API_BASE}/authors/my-data`, {
                        method: "GET",
                        headers: {
                            Authorization: `Bearer ${user.token}`,
                        },
                        signal: controller.signal,
                    }),
                ]);

                // ✅ 유저 정보
                if (!userRes.ok) throw new Error("사용자 정보를 불러올 수 없습니다.");
                const userData = await userRes.json();
                setDetail(userData);

                if (authorRes.ok) {
                    const authorData = await authorRes.json();
                    setAuthorStatus(authorData.status); // APPLIED, ACCEPTED, REJECTED
                } else {
                    setAuthorStatus(null); // 신청 이력 없음
                }

            } catch (e) {
                if (e.name !== "AbortError") setError(e.message);
            } finally {
                setLoading(false);
            }
        })();

        return () => controller.abort();
    }, [API_BASE, user]);



    if (!user) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <p>로그인이 필요합니다.</p>
            </div>
        );
    }

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <p>로딩 중…</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <p>{error}</p>
            </div>
        );
    }

    if (!detail) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <p>잠시만 기다려주세요.</p>
            </div>
        );
    }

    const {
        username,
        email,
        isKt,
        pointHistory = [],
        viewedBooks = [],
    } = detail;


    return (
        <div className="min-h-screen flex flex-col">
            <AppHeader />

            <main className="container mx-auto px-6 py-8">
                <h2 className="text-2xl font-bold mb-4">📌 마이페이지</h2>

                <section className="bg-white border rounded-md p-6 mb-6">
                    <div className="grid grid-cols-2 gap-x-8 gap-y-4">
                        <p><strong>🧑 닉네임:</strong> {username || "익명 사용자"}</p>
                        <div className="flex items-center justify-between">
                            <p><strong>📦 구독 상태:</strong> {detail.hasActiveSubscription  ? "구독 중" : "구독 안 함"}</p>

                            {!detail.hasActiveSubscription && (
                                <button
                                    onClick={handleSubscribe}
                                    disabled={subscribing}
                                    className={`ml-2 px-3 py-1 rounded-md text-sm
                  ${subscribing ? "bg-gray-400 cursor-not-allowed" : "bg-blue-600 text-white"}`}
                                >
                                    {subscribing ? "요청 중..." : "구독 신청"}
                                </button>
                            )}
                        </div>

                        <p><strong>📧 이메일:</strong> {email}</p>
                        {/*TODO: 작가 신청 버튼 연동 & 새로고침 필요*/}
                        <div className="flex items-center justify-between">
                            <p><strong>✍️ 작가 여부:</strong> {authorStatus === "ACCEPTED" ? "작가입니다" : "아직 아닙니다"}</p>

                            {(authorStatus === null || authorStatus === "REJECTED") && (
                                <>
                                    <button
                                        className="ml-2 px-3 py-1 bg-green-600 text-white text-sm rounded-md"
                                        onClick={() => setShowAuthorModal(true)}
                                    >
                                        작가 신청
                                    </button>

                                    {showAuthorModal && (
                                        <AuthorApplyModal
                                            onClose={() => setShowAuthorModal(false)}
                                            onSubmit={handleSubmitAuthorApply}
                                            isSubmitting={applyingAuthor}
                                        />
                                    )}
                                </>
                            )}
                        </div>

                        <p><strong>📱 KT 회원 여부:</strong> {isKt ? "예" : "아니오"}</p>
                        <p><strong>💰 포인트 잔액:</strong> 0</p>
                    </div>
                </section>
            </main>
        </div>
    );
}
