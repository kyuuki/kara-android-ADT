package jp.kyuuki.kara.android.model;

import java.util.EnumMap;
import java.util.Map;

public abstract class Member {
    public static enum CurrentMember {
        GYURI,
        SEUNGYEON,
        NICOLE,
        HARA,
        JIYOUNG
    }
    
    public static Map<CurrentMember, Member> currentMemberMap = new EnumMap<CurrentMember, Member>(CurrentMember.class);
    
    static {
        currentMemberMap.put(CurrentMember.GYURI,     new MemberGyuri());
        currentMemberMap.put(CurrentMember.SEUNGYEON, new MemberSeungyeon());
        currentMemberMap.put(CurrentMember.NICOLE,    new MemberNicole());
        currentMemberMap.put(CurrentMember.HARA,      new MemberHara());
        currentMemberMap.put(CurrentMember.JIYOUNG,   new MemberJiyoung());
    }

    public String stage_name_japanese;
}
