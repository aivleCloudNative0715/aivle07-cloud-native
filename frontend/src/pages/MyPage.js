import React from "react";
import AppHeader from "../components/AppHeader";
import { useAuth } from "../context/AuthContext"; // ✅ 추가

export default function MyPage() {
    const { user } = useAuth(); // ✅ Context에서 로그인 사용자 정보 불러오기

    if (!user) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <p>로그인이 필요합니다.</p>
            </div>
        );
    }

    // 임시 하드코딩 예시 대신 실제 user 정보
    const userInfo = {
        username: user.username || "익명 사용자",
        email: user.email,
        isKt: user.isKt,
        subscribed: user.subscribed ?? false,
        pointHistory: user.pointHistory || [],
        viewedBooks: user.viewedBooks || [],
    };

    return (
        <div className="min-h-screen flex flex-col">
            <AppHeader /> {/* ✅ props 제거 */}

            <main className="container mx-auto px-6 py-8">
                <h2 className="text-2xl font-bold mb-4">📌 마이페이지</h2>

                <div className="bg-white border rounded-md p-6 mb-6">
                    <p><strong>닉네임:</strong> {userInfo.username}</p>
                    <p><strong>이메일:</strong> {userInfo.email}</p>
                    <p><strong>KT 회원 여부:</strong> {userInfo.isKt ? "예" : "아니오"}</p>
                    <p><strong>구독 상태:</strong> {userInfo.subscribed ? "구독 중" : "구독 안 함"}</p>
                    {!userInfo.subscribed && (
                        <button className="mt-2 px-4 py-2 bg-blue-600 text-white rounded-md">
                            구독 신청
                        </button>
                    )}
                </div>

                <div className="mb-6">
                    <h3 className="text-xl font-semibold mb-2">📘 열람한 책</h3>
                    <ul className="list-disc list-inside">
                        {userInfo.viewedBooks.map((book, i) => (
                            <li key={i}>{book.title}</li>
                        ))}
                    </ul>
                </div>

                <div>
                    <h3 className="text-xl font-semibold mb-2">💰 포인트 내역</h3>
                    <ul className="list-disc list-inside">
                        {userInfo.pointHistory.map((item, i) => (
                            <li key={i}>
                                {item.description}: {item.amount}P
                            </li>
                        ))}
                    </ul>
                </div>
            </main>
        </div>
    );
}
