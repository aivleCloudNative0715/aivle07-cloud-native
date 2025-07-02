import React from "react";
import AppHeader from "../components/AppHeader";

export default function MyPage() {
    // TODO: 실제 사용자 정보는 API 또는 context에서 불러오세요
    const userInfo = {
        username: "jenny",
        email: "jenny@example.com",
        isKt: true,
        subscribed: false,
        pointHistory: [
            { id: 1, description: "책 구매", amount: -300 },
            { id: 2, description: "가입 포인트", amount: 500 },
        ],
        viewedBooks: [
            { id: 1, title: "열람한 책 1" },
            { id: 2, title: "열람한 책 2" },
        ],
    };

    return (
        <div className="min-h-screen flex flex-col">
            <AppHeader isLoggedIn={true} isAuthor={false} />

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
                        {userInfo.viewedBooks.map((book) => (
                            <li key={book.id}>{book.title}</li>
                        ))}
                    </ul>
                </div>

                <div>
                    <h3 className="text-xl font-semibold mb-2">💰 포인트 내역</h3>
                    <ul className="list-disc list-inside">
                        {userInfo.pointHistory.map((item) => (
                            <li key={item.id}>
                                {item.description}: {item.amount}P
                            </li>
                        ))}
                    </ul>
                </div>
            </main>
        </div>
    );
}