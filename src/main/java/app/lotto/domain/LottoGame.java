package app.lotto.domain;

import app.lotto.view.LottoResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LottoGame {

    public static List<LottoResult> processLottoGame(List<LottoTicket> allShuffledNumbers, List<Integer> winningNumbers, int bonusNumber) {
        Map<LottoPrize, Integer> statistics = getStatistics(allShuffledNumbers, winningNumbers, bonusNumber);
        return getLottoResults(statistics);
    }

    public static long getTotalProfit(List<LottoResult> lottoResults) {
        long totalProfit = 0;
        for (LottoResult result : lottoResults) {
            totalProfit += (result.getLottoPrize().getPrizeMoney() * result.getWinningCaseCount());
        }
        return totalProfit;
    }

    private static List<LottoResult> getLottoResults(Map<LottoPrize, Integer> statistics) {
        List<LottoResult> lottoResults = new ArrayList<>();

        for (LottoPrize lottoPrize : LottoPrize.values()) {
            lottoResults.add(getLottoResult(statistics, lottoPrize, lottoPrize.getPrizeMoney()));
        }
        return lottoResults;
    }

    private static LottoResult getLottoResult(Map<LottoPrize, Integer> statistics, LottoPrize lottoPrize, long prizeMoney) {
        int winningCaseCount = statistics.getOrDefault(lottoPrize, 0);

        return new LottoResult(lottoPrize, winningCaseCount);
    }

    private static Map<LottoPrize, Integer> getStatistics(List<LottoTicket> allShuffledNumbers, List<Integer> winningNumbers, int bonusNumber) {
        Map<LottoPrize, Integer> statistics = new HashMap<>();

        for (LottoTicket shuffledNumber : allShuffledNumbers) {
            int sameNumberCount = getSameNumberCount(shuffledNumber, winningNumbers);
            boolean isBonus = shuffledNumber.contains(bonusNumber);

            addCountToStatistics(statistics, sameNumberCount, isBonus);
        }
        return statistics;
    }

    private static void addCountToStatistics(Map<LottoPrize, Integer> statistics, int sameNumberCount, boolean isBonus) {
        if (LottoPrize.isLottoPrize(sameNumberCount)) {
            LottoPrize lottoPrize = LottoPrize.findLottoPrize(sameNumberCount, isBonus)
                    .orElseThrow(() -> new IllegalStateException("당첨 여부 확인 중 오류가 발생하였습니다."));
            int value = statistics.getOrDefault(lottoPrize, 0);
            value++;
            statistics.put(lottoPrize, value);
        }
    }

    private static int getSameNumberCount(LottoTicket shuffledNumbers, List<Integer> winningNumbers) {
        int count = 0;

        for (int i = 0; i < shuffledNumbers.getSize(); i++) {
            count = getCount(winningNumbers, count, shuffledNumbers.getNumber(i));
        }

        return count;
    }

    private static int getCount(List<Integer> winningNumbers, int count, int number) {
        if (winningNumbers.contains(number)) {
            count++;
        }
        return count;
    }

}