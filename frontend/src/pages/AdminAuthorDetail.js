import React, { useEffect, useState } from "react";
import {useLocation, useParams} from "react-router-dom";
import AppHeader from "../components/AppHeader";
import { useAuth } from "../context/AuthContext";
import { Button } from "../components/ui/button";

export default function AdminAuthorDetail() {
    const { id } = useParams();
    const { user } = useAuth();
    const API_BASE = process.env.REACT_APP_API_URL;
    const [author, setAuthor] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [approving, setApproving] = useState(false);
    const location = useLocation();
    const passedAuthor = location.state;

    useEffect(() => {
        if (passedAuthor) {
            setAuthor(passedAuthor);
            setLoading(false);
        } else {
            // TODO: Fallback - 실제 API 호출 등
        }
    }, [passedAuthor]);

    const handleApprove = async () => {
        setApproving(true);
        try {
            const res = await fetch(`${API_BASE}/authors/judge`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `${user.tokenType ?? "Bearer"} ${user.token}`,
                },
                body: JSON.stringify({
                    userId: author.userId,
                    isApproved: true,
                }),
            });

            if (!res.ok) throw new Error("승인 실패");

            setAuthor((prev) => ({ ...prev, isApproved: true }));
        } catch (e) {
            alert("승인 중 오류 발생");
        } finally {
            setApproving(false);
        }
    };

    return (
        <div className="min-h-screen flex flex-col">
            <AppHeader />
            <main className="container mx-auto px-6 py-10 max-w-3xl">
                <h2 className="text-2xl font-bold mb-6">작가 상세 정보</h2>

                {loading && <p>불러오는 중...</p>}
                {error && <p className="text-red-600">{error}</p>}

                {author && (
                    <div className="space-y-4">
                        <div><strong>ID:</strong> {author.id}</div>
                        <div><strong>이름:</strong> {author.authorName}</div>
                        <div><strong>이메일:</strong> {author.email}</div>
                        <div><strong>대표 작품:</strong> {author.representativeWork || "없음"}</div>
                        <div><strong>포트폴리오:</strong> {author.portfolio || "없음"}</div>
                        <div><strong>소개:</strong> {author.bio || "없음"}</div>
                        <div>
                            <strong>승인 상태:</strong>{" "}
                            {author.isApproved === null
                                ? "⌛ 승인 요청"
                                : author.isApproved
                                    ? "✅ 승인 완료"
                                    : "⏺️ 승인 보류"}
                        </div>

                        {/*TODO: 승인 버튼 연동*/}
                        {/* 승인 버튼 */}
                        {!author.isApproved && (
                            <Button onClick={handleApprove} disabled={approving}>
                                {approving ? "승인 중..." : "✅ 승인하기"}
                            </Button>
                        )}
                    </div>
                )}
            </main>
        </div>
    );
}
