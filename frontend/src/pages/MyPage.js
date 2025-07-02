// src/pages/MyPage.jsx
import React, { useEffect, useState } from "react";
import AppHeader from "../components/AppHeader";
import { useAuth } from "../context/AuthContext";

export default function MyPage() {
    const { user } = useAuth();
    const [detail, setDetail] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const API_BASE = process.env.REACT_APP_API_URL;
    const [subscribing, setSubscribing] = useState(false);

    const handleSubscribe = async () => {
        if (subscribing) return;           // 중복 클릭 방지
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

            // 성공 시 최신 사용자 정보로 덮어쓰기 (옵션)
            const updated = await res.json();      // { id, email, isAuthor, hasActiveSubscription, ... }
            setDetail((prev) => ({ ...prev, subscribed: updated.hasActiveSubscription }));

            alert("구독 신청이 완료되었습니다!");
        } catch (e) {
            alert(e.message || "구독 신청 중 오류가 발생했습니다.");
        } finally {
            setSubscribing(false);
        }
    };

    useEffect(() => {
        if (!user || !user.token) return;

        const controller = new AbortController();

        (async () => {
            try {
                const res = await fetch(`${API_BASE}/users/${user.userId}`, {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `${user.tokenType ?? "Bearer"} ${user.token}`,
                    },
                    signal: controller.signal,
                });

                if (!res.ok) throw new Error("사용자 정보를 불러올 수 없습니다.");
                const data = await res.json();
                setDetail(data);
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
                            <p><strong>✍️ 작가 여부:</strong> {detail.isAuthor ? "작가입니다" : "아직 아닙니다"}</p>
                            {!detail.isAuthor && (
                                <button className="ml-2 px-3 py-1 bg-green-600 text-white text-sm rounded-md">
                                    작가 신청
                                </button>
                            )}
                        </div>

                        <p><strong>📱 KT 회원 여부:</strong> {isKt ? "예" : "아니오"}</p>
                        <p><strong>💰 포인트 잔액:</strong> 0</p>
                    </div>
                </section>




                <section className="mb-6">
                    <h3 className="text-xl font-semibold mb-2">📘 열람한 책</h3>
                    {viewedBooks.length === 0 ? (
                        <p>열람한 책이 없습니다.</p>
                    ) : (
                        <ul className="list-disc list-inside">
                            {viewedBooks.map((b, i) => (
                                <li key={i}>{b.title}</li>
                            ))}
                        </ul>
                    )}
                </section>
            </main>
        </div>
    );
}
